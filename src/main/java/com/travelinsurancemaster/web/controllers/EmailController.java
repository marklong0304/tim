package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.dto.email.ContactForm;
import com.travelinsurancemaster.model.dto.json.JsonIndexStepResult;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.mail.MessagingException;


@Controller
@RequestMapping("/contact")
@Scope(value = "request")
public class EmailController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private EmailService emailService;

    @ResponseBody
    @PostMapping(value = "/sendEmail")
    public Boolean postStep1FormAjax(Model model, @ModelAttribute("contactForm") ContactForm contactForm,
                                     BindingResult bindingResult) {
        log.debug("sending mail " + contactForm);
        try {
            emailService.sendContactMail(contactForm.getEmailFrom(), contactForm.getName(), contactForm.getMessageBody());
        } catch (MessagingException e) {
            log.error("Error while sending contact mail", e);
            return false;
        }

        return true;
    }
}
