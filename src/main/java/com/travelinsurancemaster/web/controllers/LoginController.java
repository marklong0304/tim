package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.security.LoginStatus;
import com.travelinsurancemaster.services.security.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chernov Artur on 12.05.15.
 */

@Controller
public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String getLoginPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "security/login";
    }

    @RequestMapping(value = "/api/login.json", method = RequestMethod.GET)
    @ResponseBody
    public LoginStatus getStatus() {
        return loginService.getStatus();
    }

    @RequestMapping(value = "/api/login.json", method = RequestMethod.POST)
    @ResponseBody
    public LoginStatus login(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             HttpServletRequest request, HttpServletResponse response) {
        return loginService.login(username, password, request, response);
    }

    @RequestMapping(value = "/api/test.json", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> test() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "ok");
        return result;
    }

    @RequestMapping(value = "/api/checkAuthentication.json", method = RequestMethod.POST)
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_CONTENT_MANAGER"})
    public Map<String, String> checkAuthentication() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "ok");
        return result;
    }

}