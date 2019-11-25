package com.travelinsurancemaster.services.cms;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.repository.cms.CertificateRepository;
import com.travelinsurancemaster.services.AppHomeService;
import com.travelinsurancemaster.services.PolicyMetaCodeService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.util.ServiceUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Chernov Artur on 16.12.2015.
 */

@Service
@Transactional
public class CertificateService {
    private static final Logger log = LoggerFactory.getLogger(CertificateService.class);

    public static final String DEFAULT_MIME_TYPE = "application/pdf";
    public static final String CERTIFICATE_URL_PATH = "/cms/certificate/show/";

    @Value("${application.hostname}")
    private String host;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private AppHomeService appHomeService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserService userService;

    @Autowired
    private PolicyMetaCodeService policyMetaCodeService;

    @Transactional(readOnly = true)
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public Certificate getDefaultCertificate(PolicyMeta policyMeta, String policyCode) {
        return getDefaultCertificate(policyMeta.getVendor().getCode(), policyCode);
    }

    @Transactional(readOnly = true)
    public Certificate getDefaultCertificate(String vendorCode, String policyCode) {
        List<Certificate> defaultCertificateList = certificateRepository.findByVendorCodeAndPolicyMetaCodeAndDefaultPolicyTrueAndDeletedFalse(vendorCode, policyCode);
        if (CollectionUtils.isNotEmpty(defaultCertificateList)) {
            return defaultCertificateList.get(0);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public File getCertificateFile(String uuid) {
        Certificate certificate = certificateRepository.findByUuidAndDeletedFalse(uuid);
        return certificate == null ? null : getFileByCertificate(certificate);
    }

    @Transactional(readOnly = true)
    public Certificate getCertificateById(Long id) {
        return certificateRepository.findByIdAndDeletedFalse(id);
    }

    @Transactional(readOnly = true)
    public Certificate getCertificateByUuid(String uuid) {
        return certificateRepository.findByUuidAndDeletedFalse(uuid);
    }

    @Transactional(readOnly = true)
    public List<Certificate> getCertificatesByPolicyMeta(PolicyMeta policyMeta, boolean defaultPolicyMeta, String policyCode) {
        return getCertificateByVendorCodeAndPolicyMetaCode(policyMeta.getVendor().getCode(), policyCode, defaultPolicyMeta);
    }

    @Transactional(readOnly = true)
    public List<Certificate> getCertificateByVendorCode(String vendorCode) {
        return certificateRepository.findByVendorCodeAndDeletedFalse(vendorCode, ServiceUtils.sortByFieldAscIgnoreCase("fileName"));
    }

    @Transactional(readOnly = true)
    public List<Certificate> getCertificateByVendorCodeAndPolicyMetaCode(String vendorCode, String policyMetaCode) {
        return certificateRepository.findByVendorCodeAndPolicyMetaCodeAndDeletedFalse(vendorCode, policyMetaCode, ServiceUtils.sortByFieldAscIgnoreCase("fileName"));
    }

    @Transactional(readOnly = true)
    public List<Certificate> getCertificateByVendorCodeAndPolicyMetaCode(String vendorCode, String policyMetaCode, boolean defaultPolicyMeta) {
        return certificateRepository.findByVendorCodeAndPolicyMetaCodeAndDefaultPolicyAndDeletedFalse(vendorCode, policyMetaCode, defaultPolicyMeta, ServiceUtils.sortByFieldAscIgnoreCase("fileName"));
    }

    private File getFileByCertificate(Certificate certificate) {
        String certificateFilePath = appHomeService.getCertificatePath(certificate);
        return appHomeService.fileRestore(certificateFilePath);
    }

    public Certificate save(Certificate certificate, MultipartFile file) {
        Certificate newCertificate = save(certificate);
        if (file != null && !file.isEmpty()) {
            String certificatePath = appHomeService.getCertificatePath(newCertificate);
            appHomeService.fileUpload(file, certificatePath);
        }
        return newCertificate;
    }

    public Certificate updateCertificate(String uuid, MultipartFile file, String fileName, String vendorCode, String policyMetaCode, Set<CountryCode> countries, Set<StateCode> states, boolean defaultPolicy) {
        Certificate certificate = getCertificateByUuid(uuid);
        certificate.setFileName(fileName);
        certificate.setVendorCode(vendorCode);
        certificate.setPolicyMetaCode(policyMetaCode);
        certificate.setCountries(countries);
        certificate.setStates(states);
        certificate.setDefaultPolicy(defaultPolicy);
        if (file != null) {
            certificate.setSize(file.getSize());
            certificate.setMimeType(file.getContentType());
        }
        return save(certificate, file);
    }

    public Certificate addCertificate(MultipartFile file, String fileName, String vendorCode, String policyMetaCode, Set<CountryCode> countries, Set<StateCode> states, boolean defaultPolicy) {
        Certificate certificate = new Certificate();
        certificate.setFileName(fileName);
        certificate.setVendorCode(vendorCode);
        certificate.setPolicyMetaCode(policyMetaCode);
        certificate.setCountries(countries);
        certificate.setStates(states);
        certificate.setDefaultPolicy(defaultPolicy);
        certificate.setSize(file.getSize());
        certificate.setMimeType(file.getContentType());
        return save(certificate, file);
    }

    @Transactional(readOnly = true)
    public boolean hasCertificate(QuoteRequest quoteRequest, PolicyMeta policyMeta, HttpServletRequest httpServletRequest) {
        return getCertificate(quoteRequest, policyMeta, httpServletRequest) != null;
    }

    private Certificate save(Certificate certificate) {
        Certificate newCertificate;
        certificate.setModifiedDate(new Date());
        if (certificate.getId() != null && (newCertificate = certificateRepository.findOne(certificate.getId())) != null) {
            //newCertificate.setModifiedDate(certificate.getModifiedDate());
            newCertificate.setFileName(certificate.getFileName());
            newCertificate.setMimeType(certificate.getMimeType());
            newCertificate.setSize(certificate.getSize());
            newCertificate.setDeleted(certificate.isDeleted());
            newCertificate.setVendorCode(certificate.getVendorCode());
            newCertificate.setPolicyMetaCode(certificate.getPolicyMetaCode());
            newCertificate.setDefaultPolicy(certificate.isDefaultPolicy());
            newCertificate.setCountries(certificate.getCountries());
            newCertificate.setStates(certificate.getStates());
            return certificateRepository.save(newCertificate);
        }
        if (StringUtils.isBlank(certificate.getUuid())) {
            certificate.setUuid(UUID.randomUUID().toString());
        }
        if (certificate.getCreateDate() == null) {
            certificate.setCreateDate(new Date());
        }
        if (certificate.getAuthor() == null) {
            certificate.setAuthor(SecurityHelper.getCurrentUser() != null ? SecurityHelper.getCurrentUser() : userService.getAll().iterator().next());
        }
        return certificateRepository.save(certificate);
    }

    public Certificate remove(Certificate certificate) {
        certificate.setDeleted(true);
        return save(certificate);
    }

    public Certificate removeById(Long id) {
        Certificate certificate = getCertificateById(id);
        certificate.setDeleted(true);
        return certificateRepository.save(certificate);
    }

    public Certificate removeByUuid(String uuid) {
        Certificate certificate = certificateRepository.findByUuidAndDeletedFalse(uuid);
        certificate.setDeleted(true);
        return certificateRepository.save(certificate);
    }

    public List<Certificate> removeByVendorAndPolicy(String vendorCode, String policyMetaCode) {
        List<Certificate> certificates = certificateRepository.findByVendorCodeAndPolicyMetaCodeAndDeletedFalse(vendorCode, policyMetaCode, ServiceUtils.sortByFieldAscIgnoreCase("fileName"));
        for (Certificate certificate : certificates) {
            certificate.setDeleted(true);
        }
        return certificateRepository.save(certificates);
    }

    public List<Certificate> removeByVendor(String vendorCode) {
        List<Certificate> certificates = certificateRepository.findByVendorCodeAndDeletedFalse(vendorCode, ServiceUtils.sortByFieldAscIgnoreCase("fileName"));
        for (Certificate certificate : certificates) {
            certificate.setDeleted(true);
        }
        return certificateRepository.save(certificates);
    }

    @Transactional(readOnly = true)
    public Certificate getCertificate(QuoteRequest quoteRequest, PolicyMeta policyMeta, HttpServletRequest httpServletRequest) {
        return getCertificate(quoteRequest, policyMeta, httpServletRequest, true);
    }

    @Transactional(readOnly = true)
    public Certificate getCertificate(QuoteRequest quoteRequest, PolicyMeta policyMeta, HttpServletRequest httpServletRequest, boolean defaultIfNotFound) {
        LocationService.CountryStatePair countryStatePair = null;
        if (quoteRequest != null && quoteRequest.getResidentCountry() != null) {
            countryStatePair = new LocationService.CountryStatePair(quoteRequest.getResidentCountry(), quoteRequest.getResidentState());
        } else if (httpServletRequest != null) {
            countryStatePair = locationService.getCountryStatePair(httpServletRequest);
        }
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), quoteRequest);
        if (countryStatePair != null) {
            List<Certificate> certificates = getCertificateByVendorCodeAndPolicyMetaCode(policyMeta.getVendor().getCode(), policyMetaCode.getCode(), false);
            for (Certificate certificate : certificates) {
                if (certificate.getCountries().contains(countryStatePair.getCountryCode())) {
                    if (countryStatePair.getStateCode() != null) {
                        if (certificate.getStates().contains(countryStatePair.getStateCode())) {
                            return certificate;
                        }
                    } else {
                        return certificate;
                    }
                }
            }
        }
        return defaultIfNotFound ? getDefaultCertificate(policyMeta.getVendor().getCode(), policyMetaCode.getCode()) : null;
    }

    @Transactional(readOnly = true)
    public File getCertificateFile(QuoteRequest quoteRequest, PolicyMeta policyMeta, HttpServletRequest httpServletRequest) {
        Certificate certificate = getCertificate(quoteRequest, policyMeta, httpServletRequest);
        return certificate == null ? null : getFileByCertificate(certificate);
    }

    @Transactional(readOnly = true)
    public String getCertificateUrl(QuoteRequest quoteRequest, PolicyMeta policyMeta, HttpServletRequest httpServletRequest) {
        Certificate certificate = getCertificate(quoteRequest, policyMeta, httpServletRequest);
        if (certificate == null) {
            return StringUtils.EMPTY;
        }
        return /*host +*/ CERTIFICATE_URL_PATH + certificate.getUuid();
    }
}
