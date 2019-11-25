package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.dto.validator.UserInfoValidator;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.security.validator.CompanyValidator;
import com.travelinsurancemaster.model.security.validator.UserUpdateValidator;
import com.travelinsurancemaster.services.CompanyService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author Alexander.Isaenco
 */
@Controller
@RequestMapping(value = "/accountInfo")
public class AccountInfoController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserInfoValidator userInfoValidator;

    @Autowired
    private UserUpdateValidator userUpdateValidator;

    @Autowired
    private CompanyValidator companyValidator;

    @RequestMapping(method = RequestMethod.GET)
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_CONTENT_MANAGER"})
    public String getAccountInfo(Model model) {
        User user = SecurityHelper.getCurrentUser();
        if (user == null) {
            return "redirect:/404";
        }
        User storedUser = userService.get(user.getId());
        model.addAttribute("user", storedUser);
        model.addAttribute("affiliateLink", "");
        return "account_info";
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.POST, params = {"generateAffiliateLink"})
    @ResponseBody
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_CONTENT_MANAGER"})
    public String generateAffiliateLink(@PathVariable("userId") Long userId) {
        return userService.generateAffiliateLink(userId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_CONTENT_MANAGER"})
    public String updateAccountInfo(@Valid @ModelAttribute("user") User user, @RequestParam Map<String, String> params, BindingResult bindingResult, Model model) {

        log.debug("Update user={}, bindingResult = {}", user.getEmail(), bindingResult);

        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser == null || !currentUser.getId().equals(user.getId())) {
            throw new IllegalArgumentException("Wrong user id!");
        }
        user.getUserInfo().setCompany(currentUser.getUserInfo().isCompany());
        user.setVerified(currentUser.isVerified());
        userUpdateValidator.validateUserInfo(user, bindingResult);
        userInfoValidator.validate(user.getUserInfo(), bindingResult);
        ValidationUtils.validatePhone(bindingResult, user.getUserInfo().getPhone(), "userInfo.phone");
        if (bindingResult.hasErrors()) {
            return "account_info";
        }
        user = userService.updateSimpleUser(user);
        if (currentUser != null) {
            currentUser.setName(user.getName());
            currentUser.getUserInfo().setLastName(user.getUserInfo().getLastName());
        }
        model.addAttribute("user", user);
        addMessageSuccess("Saved", model);
        return "account_info";
    }

    @RequestMapping(value = "sendUpdateEmail", method = RequestMethod.POST)
    @ResponseBody
    public String sendUpdateEmailToUser() {
        User user = SecurityHelper.getCurrentUser();
        if (user == null) {
            throw new IllegalArgumentException("Wrong user id!");
        }
        userService.sendUpdateUserEmail(user);
        return "Update email is sent!";
    }
}
