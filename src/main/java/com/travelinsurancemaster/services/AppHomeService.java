package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;

/**
 * Created by Chernov Artur on 14.12.2015.
 */
@Service
public class AppHomeService {

    private static final String CERTIFICATE_PATH = "certificate" + File.separator;

    @Value("${app.home}")
    private String rootPath;

    @PostConstruct
    private void init() throws IOException {
        File dir = new File(rootPath + CERTIFICATE_PATH);
        FileUtils.forceMkdir(dir);
    }

    public String getCertificatePath(Certificate certificate) {
        return getCertificatePath(certificate.getVendorCode(), certificate.getPolicyMetaCode(), certificate.getUuid());
    }

    public String getCertificatePath(String vendorCode, String policyMetaCode, String fileName) {
        return rootPath + CERTIFICATE_PATH + vendorCode + File.separator + policyMetaCode + File.separator + fileName;
    }

    public String fileUpload(MultipartFile file, String path) {
        File newFile = new File(path);
        try {
            FileUtils.forceMkdir(newFile.getParentFile());
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            try (InputStream inputStream = new BufferedInputStream(file.getInputStream());
                 OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newFile))) {
                IOUtils.copyLarge(inputStream, outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("fileUpload " + path, e);
        }
        return newFile.getAbsolutePath();
    }

    public File fileRestore(String path) {
        return new File(path);
    }


}
