package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.services.clients.AllianzClient;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * Created by Raman on .
 */

@Ignore
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class AllianzClientTest {
    private static final Logger log = LoggerFactory.getLogger(AllianzClientTest.class);

    public static final String SUCCESS_CODE = "SUCCESS";

    @Autowired
    private AllianzClient client;

    @Autowired
    private InsuranceMasterApiProperties apiProperties;

    @Test
    public void quoteTest() throws IOException {
   }

    @Test
    public void bookTest() throws IOException, JAXBException {
    }

}