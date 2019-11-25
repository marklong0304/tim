package com.travelinsurancemaster.web;

import com.travelinsurancemaster.InsuranceMasterApp;
import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.FileAttachmentUtils;
import com.travelinsurancemaster.util.JsonUtils;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

;
;

/**
 * Created by Chernov Artur on 17.12.2015.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
@WebAppConfiguration
public class PolicyMetaCertificateRestControllerTest {

    private static final String CERTIFICATE_PATH = "certificate" + File.separator;

    private static final String LIST_CERTIFICATE_URL = "/api/certificate/list";
    private static final String CREATE_CERTIFICATE_URL = "/api/certificate/create";
    private static final String UPDATE_CERTIFICATE_URL = "/api/certificate/update";
    private static final String DELETE_CERTIFICATE_URL = "/api/certificate/delete";

    private static final String DEFAULT_FILE_NAME = "test";
    private static final String DEFAULT_VENDOR_CODE = "CSA";
    private static final String DEFAULT_POLICY_META_CODE = "CUSTOM";

    @Autowired
    private WebApplicationContext wac;

    @Value("${app.home}")
    private String rootPath;

    private MockMultipartFile testFile = new MockMultipartFile("file", "test.pdf", FileAttachmentUtils.PDF_MEDIA_TYPE, "some text".getBytes());

    @PostConstruct
    private void init() {
        JsonUtils.setDateFormat(DateUtil.DEFAULT_DATE_FORMAT);
    }

    @Test
    public void testDefault() throws Exception {
        ResultActions mockResult = createAndFillMockMVC(DEFAULT_FILE_NAME, DEFAULT_VENDOR_CODE, DEFAULT_POLICY_META_CODE, StringUtils.EMPTY, StringUtils.EMPTY, "true", CREATE_CERTIFICATE_URL);
        MvcResult result = mockResult.andExpect(status().is(200)).andReturn();
        Certificate certificate = JsonUtils.getObject(result.getResponse().getContentAsString(), Certificate.class);
        assertNotNull(certificate);
    }

    @Test
    public void testCustomStatesAndCountries() throws Exception {
        ResultActions mockResult = createAndFillMockMVC(DEFAULT_FILE_NAME, DEFAULT_VENDOR_CODE, DEFAULT_POLICY_META_CODE, "US,CA", "WA,NJ", "false", CREATE_CERTIFICATE_URL);
        MvcResult result = mockResult.andExpect(status().is(200)).andReturn();
        Certificate certificate = JsonUtils.getObject(result.getResponse().getContentAsString(), Certificate.class);
        assertNotNull(certificate);
    }

    @Test
    public void testEmptyVendorCode() throws Exception {
        ResultActions mockResult = createAndFillMockMVC(DEFAULT_FILE_NAME, StringUtils.EMPTY, DEFAULT_POLICY_META_CODE, "US,CA", "WA,NJ", "false", CREATE_CERTIFICATE_URL);
        MvcResult result = mockResult.andExpect(status().is(200)).andReturn();
        assertEquals("{\"errors\":[{\"message\":\"Vendor code is empty\",\"code\":\"1005\"}]}", result.getResponse().getContentAsString());
    }

    @Ignore
    public void down() throws Exception {
        Thread.sleep(1000);
        Path directory = Paths.get(rootPath + CERTIFICATE_PATH);
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    private ResultActions createAndFillMockMVC(String fileName, String vendorCode, String policyMetaCode, String countries, String states, String defaultPolicy, String url) throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        ResultActions mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.fileUpload(url)
                .file(testFile)
                .param("fileName", fileName)
                .param("vendorCode", vendorCode)
                .param("policyMetaCode", policyMetaCode)
                .param("countries", countries)
                .param("states", states)
                .param("defaultPolicy", defaultPolicy));
        return mockMvcResult;
    }
}
