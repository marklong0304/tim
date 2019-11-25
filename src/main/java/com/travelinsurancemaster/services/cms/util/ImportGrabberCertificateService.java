package com.travelinsurancemaster.services.cms.util;

import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.cms.CertificateService;
import com.travelinsurancemaster.services.security.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Alexander.Isaenco
 */
@Service
@Transactional
public class ImportGrabberCertificateService {
    private static final Logger log = LoggerFactory.getLogger(ImportGrabberCertificateService.class);

    public static final String GRABBER_LIST = "api/certificates/list";

    @Autowired
    private UserService userService;

    @Autowired
    private CertificateService certificateService;

    @Value("${certificate.grabber.download.threads}")
    private int downloadThreads;

    @Value("${certificate.grabber.url}")
    private String grabberUrl;


    private final static AtomicBoolean inProgress = new AtomicBoolean(false);

    public void importCertificatesFromGrabber() {
        if (!inProgress.compareAndSet(false, true)) {
            throw new IllegalArgumentException("Importing is in progress");
        }
        try {

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<GrabberCertificate[]> responseEntity = restTemplate.getForEntity(getGrabberUrl(), GrabberCertificate[].class);
            GrabberCertificate[] list = responseEntity.getBody();
            HttpStatus statusCode = responseEntity.getStatusCode();
            log.info("importCertificatesFromGrabber statusCode: {}, list size {}", statusCode, list == null ? 0 : list.length);

            if (list == null) {
                throw new IllegalArgumentException("Empty result from certificate grabber");
            }

            processCertificates(list);
        } finally {
            inProgress.set(false);
        }
    }

    private String getGrabberUrl() {
        return grabberUrl + (grabberUrl.endsWith("/") ? "" : "/") + GRABBER_LIST;
    }

    private void processCertificates(GrabberCertificate[] list, String... skipVendors) {
        //1: build GrabberCertificate Map<VendorCode-ProductCode, Map<file, List<Certificate>>
        Map<String, Map<String, List<GrabberCertificate>>> certificateProductFileMap = new HashMap<>();
        for (GrabberCertificate grabberCertificate : list) {
            String download_file = grabberCertificate.getDownload_file();

            if (ArrayUtils.isNotEmpty(skipVendors) && ArrayUtils.contains(skipVendors, grabberCertificate.getVendor_code())) {
                continue;
            }

            String code = grabberCertificate.getCode();
            if (!certificateProductFileMap.containsKey(code)) {
                certificateProductFileMap.put(code, new HashMap<>());
            }
            if (!certificateProductFileMap.get(code).containsKey(download_file)) {
                certificateProductFileMap.get(code).put(download_file, new ArrayList<>());
            }
            certificateProductFileMap.get(code).get(download_file).add(grabberCertificate);
        }
        log.debug("certificateProductFileMap, products: {}", certificateProductFileMap.keySet());

        //2: build Certificates for each product
        User certAuthor = userService.getCertGrabberUser();
        if (certAuthor == null) {
            throw new IllegalArgumentException("User is null: " + UserService.CERTIFICATE_GRABBER_EMAIL);
        }
        Map<String, List<Certificate>> certificateMap = new HashMap<>();
        for (Map.Entry<String, Map<String, List<GrabberCertificate>>> certificateProductFileMapEntry : certificateProductFileMap.entrySet()) {
            String vendorProduct = certificateProductFileMapEntry.getKey();
            if (!certificateMap.containsKey(vendorProduct)) {
                certificateMap.put(vendorProduct, new ArrayList<>());
            }
            String defaultFile = null;
            int maxSize = 0;
            Map<String, List<GrabberCertificate>> certificateFileMapEntry = certificateProductFileMapEntry.getValue();
            for (List<GrabberCertificate> grabberCertificates : certificateFileMapEntry.values()) {
                if (grabberCertificates.size() > maxSize) {
                    maxSize = grabberCertificates.size();
                    defaultFile = grabberCertificates.get(0).getDownload_file();
                }
                Certificate certificate = null;
                for (GrabberCertificate grabberCertificate : grabberCertificates) {
                    if (certificate == null) {
                        certificate = grabberCertificate.toCertificate(certAuthor);
                    }
                    certificate.getCountries().addAll(grabberCertificate.getCountryCodes());
                    certificate.getStates().addAll(grabberCertificate.getUsaStateCodes());
                    certificate.getStates().addAll(grabberCertificate.getCanadaStateCodes());
                }
                certificateMap.get(vendorProduct).add(certificate);
                log.debug("processed for product {} cert file {} ", vendorProduct, certificate != null ? certificate.getFileName() : null);
            }
            for (Certificate certificate : certificateMap.get(vendorProduct)) {
                if (certificate.getFileName().equals(defaultFile)) {
                    certificate.setDefaultPolicy(true);
                    log.debug("DEFAULT cert for product {}: {}", vendorProduct, certificate.getFileName());
                    break;
                }
            }
        }

        //3: get file list for update
        ExecutorService downloader = Executors.newFixedThreadPool(downloadThreads);
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        //for each product
        List<Future> futures = new ArrayList<>();
        for (List<Certificate> certificates : certificateMap.values()) {
            String vendorCode = certificates.get(0).getVendorCode();
            String policyMetaCode = certificates.get(0).getPolicyMetaCode();
            log.debug("product {} {} update started", vendorCode, policyMetaCode);

            List<Certificate> certificatesToDownload = new ArrayList<>();
            List<Certificate> certificatesToUpdateMapping = new ArrayList<>();
            List<Certificate> certificatesToDelete = new ArrayList<>();

            List<Certificate> dbCertificates = certificateService.getCertificateByVendorCodeAndPolicyMetaCode(vendorCode, policyMetaCode);
            log.debug("product {} {} dbcertificates size: {}", vendorCode, policyMetaCode, dbCertificates.size());
            if (CollectionUtils.isNotEmpty(dbCertificates)) {
                for (Certificate certificate : certificates) {
                    boolean found = false;
                    Iterator<Certificate> dbCertificateIter = dbCertificates.iterator();
                    while (dbCertificateIter.hasNext()) {
                        Certificate dbCertificate = dbCertificateIter.next();
                        if (!equalsCertificateFiles(dbCertificate, certificate)) {
                            continue;
                        }
                        found = true;
                        dbCertificateIter.remove();
                        if (!equalsCertificateMapping(dbCertificate, certificate)) {
                            certificate.setId(dbCertificate.getId());
                            certificate.setUuid(dbCertificate.getUuid());
                            certificate.setSize(dbCertificate.getSize());
                            certificatesToUpdateMapping.add(certificate);
                        }
                        break;
                    }
                    if (!found) {
                        certificatesToDownload.add(certificate);
                    }
                }
                certificatesToDelete.addAll(dbCertificates);
            } else {
                certificatesToDownload.addAll(certificates);
            }
            for (Certificate certificate : certificatesToDelete) {
                log.debug("remove extra dbCertificate {}", certificate.getId());
                certificateService.removeById(certificate.getId());
            }
            for (Certificate certificate : certificatesToDownload) {
                log.debug("add to download certificate {}", certificate.getFileName());
                futures.add(downloadCertificate(downloader, securityContext, requestAttributes, certificate));
            }
            for (Certificate certificate : certificatesToUpdateMapping) {
                log.debug("update mapping dbCertificate {}", certificate.getId());
                certificateService.save(certificate, null);
            }
            log.debug("product {} {} update finished", vendorCode, policyMetaCode);
        }

        log.debug("processed all certificates, waiting to download...");
        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage(), e);
            }
        }

        log.debug("certificates update done!");

        downloader.shutdown();
    }

    private Future downloadCertificate(ExecutorService downloader, SecurityContext securityContext, RequestAttributes requestAttributes, Certificate certificate) {
        return downloader.submit(() -> {
            byte[] b;
            String lnk = "";
            try {
                lnk = grabberUrl + certificate.getFileName();
                URL url = new URL(lnk);
                log.debug("download certificate {}", certificate.getFileName());
                try (InputStream is = new BufferedInputStream(url.openStream())) {
                    b = IOUtils.toByteArray(is);
                }
            } catch (IOException e) {
                throw new RuntimeException("downloadCertificate " + lnk, e);
            }
            SecurityContextHolder.setContext(securityContext);
            RequestContextHolder.setRequestAttributes(requestAttributes, true);
            certificate.setSize(b.length);
            log.debug("download certificate {} finish, size: {}", certificate.getFileName(), b.length);
            certificateService.save(certificate, new MockMultipartFile(certificate.getFileName(), certificate.getFileName(), certificate.getMimeType(), b));
            log.debug("certificate saved {} - {}, {}", certificate.getVendorCode(), certificate.getPolicyMetaCode(), certificate.getFileName());
        });
    }

    private boolean equalsCertificateFiles(Certificate dbCertificate, Certificate certificate) {
        if (!Objects.equals(dbCertificate.getFileName(), certificate.getFileName())) {
            return false;
        }
        if (dbCertificate.getCreateDate().getTime() != certificate.getCreateDate().getTime()) {
            return false;
        }
        return true;
    }

    private boolean equalsCertificateMapping(Certificate dbCertificate, Certificate certificate) {
        if (dbCertificate.isDefaultPolicy() != certificate.isDefaultPolicy()) {
            return false;
        }
        if (!Objects.equals(dbCertificate.getCountries(), certificate.getCountries())) {
            return false;
        }
        if (!Objects.equals(dbCertificate.getStates(), certificate.getStates())) {
            return false;
        }
        return true;
    }


}
