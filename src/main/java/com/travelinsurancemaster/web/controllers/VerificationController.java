package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.services.VerificationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Chernov Artur on 12.05.15.
 */

@Controller
public class VerificationController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(VerificationController.class);

    @Autowired
    private VerificationTokenService verificationTokenService;

    @RequestMapping(value = "verification/emailRegistration/{token}", method = RequestMethod.GET)
    public String verityEmailRegistration(@PathVariable("token") String token, Model model) {
        if (!verificationTokenService.verifyEmail(token, model)) {
            return "confirmations/confirmationError";
        }
        return "confirmations/confirmationEmailVerification";
    }

    @RequestMapping(value = "restorePassword/{token}", method = RequestMethod.GET)
    public String verifyRestorePassword(@PathVariable("token") String token, Model model) {
        if (!verificationTokenService.verifyLostPassword(token)) {
            return "confirmations/confirmationError";
        }
        model.addAttribute("tokenStr", token);
        return "security/restorePassword";
    }

    @RequestMapping(value = "updateEmail/{token}", method = RequestMethod.GET)
    public String verifyUpdateEmail(@PathVariable("token") String token, Model model) {
        if (!verificationTokenService.verifyUpdateEmail(token, model)) {
            return "confirmations/confirmationError";
        }
        model.addAttribute("tokenStr", token);
        return "security/emailUpdate";
    }
}
