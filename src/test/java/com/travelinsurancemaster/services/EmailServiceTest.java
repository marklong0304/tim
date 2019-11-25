package com.travelinsurancemaster.services;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.travelinsurancemaster.InsuranceMasterApp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.Message;
import javax.mail.MessagingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

;
;

/**
 * Created by Chernov Artur on 08.05.15.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    private GreenMail testSmtp;

    @Before
    public void testSmtpInit() {
        ServerSetup setup = new ServerSetup(3025, "localhost", "smtp");
        testSmtp = new GreenMail(setup);
        testSmtp.start();
        javaMailSender.setPort(3025);
        javaMailSender.setHost("localhost");
    }

    @Test
    public void testSendMail() throws InterruptedException, MessagingException {
        emailService.sendSimpleMail("test@receiver.com", "test subject", "test message");
        assertTrue(testSmtp.waitForIncomingEmail(5000, 1));
        Message[] messages = testSmtp.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals("test subject", messages[0].getSubject());
        String body = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", "");
        assertEquals("test message", body);
    }

    @After
    public void cleanup() {
        testSmtp.stop();
    }

}