package com.travelinsurancemaster.services;

import com.travelinsurancemaster.MailConfig;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chernov Artur on 08.05.15.
 */

@Service("mailService")
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String PDF_CONTENT_TYPE = "application/pdf;charset=UTF-8";

    public static final String AUTO_REGISTRATION_MAIL = "auto_registration_mail";
    public static final String END_TRIP_MAIL = "end_trip_mail";
    public static final String PURCHASE_MAIL = "purchase_mail";
    public static final String PURCHASE_REWARD_MAIL = "purchase_reward_mail";
    public static final String REGISTRATION_MAIL = "registration_mail";
    public static final String RESTORE_PASSWORD_MAIL = "restore_password_mail";
    public static final String PASSWORD_UPDATE_MAIL = "password_update_mail";
    public static final String SEND_UPDATE_MAIL = "send_update_mail";
    public static final String UPDATE_MAIL = "update_mail";
    public static final String VERIFICATION_MAIL = "verification_mail";
    public static final String AFFILIATE_MAIL = "affiliate_mail";
    public static final String QUOTE_MISMATCH_MAIL = "quote_mismatch_mail";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private MailConfig mailConfig;

    @Async
    public void sendMail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = createMimeMessage(to, subject, body, null);
            sendMimeMessages(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendContactMail(String from, String name, String body) throws MessagingException {
        MimeMessage mimeMessage = createMimeMessage("alexn@travelinsurancemaster.com", "Contact form: " + name, "Message from " + from + "\n" + body, null);
        sendMimeMessages(mimeMessage);
    }

    @Async
    public void sendRegistrationMail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = createMimeMessage(to, subject, body, new HashMap<String, ClassPathResource>(){{
                put("logo_button4", new ClassPathResource("static/images/mails/button4.png"));
            }});
            sendMimeMessages(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async
    public void sendLoginMail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = createMimeMessage(to, subject, body, new HashMap<String, ClassPathResource>(){{
                put("logo_button1", new ClassPathResource("static/images/mails/button1.png"));
            }});
            sendMimeMessages(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async
    public void sendUpdateEmailMail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = createMimeMessage(to, subject, body, new HashMap<String, ClassPathResource>(){{
                put("logo_button2", new ClassPathResource("static/images/mails/button2.png"));
            }});
            sendMimeMessages(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async
    public void sendRestoreMail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = createMimeMessage(to, subject, body, new HashMap<String, ClassPathResource>(){{
                put("logo_button3", new ClassPathResource("static/images/mails/button3.png"));
            }});
            sendMimeMessages(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Async
    public void sendVerificationMail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = createMimeMessage(to, subject, body, new HashMap<String, ClassPathResource>(){{
                put("logo_button4", new ClassPathResource("static/images/mails/button4.png"));
            }});
            sendMimeMessages(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    public MimeMessage createMimeMessage(String to, String subject, String body, Map<String, ClassPathResource> attachments) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message;
        message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        message.setFrom(mailConfig.getFrom());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body, true);
        ClassPathResource classPathResource = new ClassPathResource("static/images/mails/mail_logo_header.png");

        message.addInline("logo_travelinsurancemaster", classPathResource);

        if (MapUtils.isNotEmpty(attachments)) {
            for (Map.Entry<String, ClassPathResource> attachment : attachments.entrySet()) {
                message.addInline(attachment.getKey(), attachment.getValue());
            }
        }
        return mimeMessage;
    }

    @Async
    public void sendMimeMessages(MimeMessage... mimeMessages) {
        try {
            javaMailSender.send(mimeMessages);
        } catch (MailException e) {
            log.error(e.getMessage(), e);
        }
    }


    @Async
    public void sendSimpleMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    private class Attachment {
        String filename;
        ByteArrayResource byteArrayResource;
        String contentType;

        public Attachment(String filename, ByteArrayResource byteArrayResource, String contentType) {
            this.filename = filename;
            this.byteArrayResource = byteArrayResource;
            this.contentType = contentType;
        }

    }

    public String getHtmlContent(String templateName, Context context) {
        String htmlContent = templateEngine.process(templateName, context);
        return htmlContent;
    }
}