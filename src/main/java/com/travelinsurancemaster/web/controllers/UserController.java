package com.travelinsurancemaster.web.controllers;

import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.model.dto.PercentInfo;
import com.travelinsurancemaster.model.dto.SalaryCorrection;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.validator.UserInfoValidator;
import com.travelinsurancemaster.model.security.CurrentUser;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.security.VerificationToken;
import com.travelinsurancemaster.model.security.validator.UserCreateValidator;
import com.travelinsurancemaster.model.security.validator.UserUpdateValidator;
import com.travelinsurancemaster.services.CompanyService;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.VerificationTokenService;
import com.travelinsurancemaster.services.datatable.report.SalaryReportService;
import com.travelinsurancemaster.services.security.LoginService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.CountryCodesUtils;
import com.travelinsurancemaster.util.SecurityHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by Chernov Artur on 17.04.15.
 */

@Controller
@RequestMapping(value = "/users")
public class UserController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserCreateValidator userCreateValidator;

    @Autowired
    private UserUpdateValidator userUpdateValidator;

    @Autowired
    private UserInfoValidator userInfoValidator;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private SalaryReportService salaryReportService;

    private void setNavigation(User user, Model model) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/users", "Users");
        if (user != null && user.getId() != null) {
            map.put("/users/edit/" + String.valueOf(user.getId()), user.getName());
        }

        model.addAttribute("breadcrumb", map);
        model.addAttribute("companies", companyService.getAll());
        model.addAttribute("backUrl", "/users");
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String getUsers(Model model) {
        log.debug("Getting users");
        List<User> users = userService.getAll();
        users.forEach(user -> {
            Company company = user.getCompany();
            List<Purchase> purchases = purchaseService.getAllByUserId(user.getId());
            List<SalaryCorrection> salaryCorrections = salaryReportService.getAllByAffiliateId(user.getId());
            user.setCanDelete(
                    purchases.isEmpty() && salaryCorrections.isEmpty()
                    && (company == null || !user.getId().equals(company.getCompanyManager() != null ? company.getCompanyManager().getId() : 0))
            );
            user.setHasBookings(purchases.stream().anyMatch(purchase -> purchase.isSuccess()));
        });
        model.addAttribute("users", users);
        return "admin/users/users";
    }

    @RequestMapping(value = "/delete/{userId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String deleteUser(@PathVariable("userId") Long userId) {
        log.debug("Delete user by id={} action", userId);
        String redirectUrl = "redirect:/users";
        User user = userService.get(userId);
        Company company = user.getCompany();
        List<Purchase> purchases = purchaseService.getAllByUserId(userId);
        List<SalaryCorrection> salaryCorrections = salaryReportService.getAllByAffiliateId(userId);
        boolean canDelete = purchases.isEmpty();
        if(!canDelete) {
            log.debug("Can't delete user (user id = " + userId + ") with purchases!");
            return redirectUrl;
        }
        canDelete = salaryCorrections.isEmpty();
        if (!canDelete) {
            log.debug("Can't delete user (user id = " + userId + ") with salary corrections!");
            return redirectUrl;
        }
        canDelete = company == null || !user.getId().equals(company.getCompanyManager() != null ? company.getCompanyManager().getId() : 0);
        if (!canDelete) {
            log.debug("Can't delete user (user id = " + userId + ") who is the company manager for " + company.getName() + "!");
            return redirectUrl;
        }
        if(canDelete) userService.delete(user);
        return redirectUrl;
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String createUser(Model model) {
        log.debug("Getting create user page");
        model.addAttribute("user", new User());
        setNavigation(new User(), model);
        return "admin/users/user_create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String handleUserCreateForm(@ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        log.debug("Processing user create user={}, bindingResult={}", user, bindingResult);
        userCreateValidator.validate(user, bindingResult);
        userCreateValidator.validateRole(user.getRoles(), bindingResult);
        if (bindingResult.hasErrors()) {
            setNavigation(new User(), model);
            return "admin/users/user_create";
        }
        try {
            userService.registration(user);
        } catch (DataIntegrityViolationException e) {
            log.warn("Exception occurred when trying to create the user, assuming duplicate email", e);
            bindingResult.reject("email.exists", "Email already exists");
            setNavigation(new User(), model);
            return "admin/users/user_create";
        }
        return "redirect:/users";
    }

    @RequestMapping(value = "/edit/{userId}", method = RequestMethod.GET)
    @Secured("ROLE_ADMIN")
    public String editUser(@PathVariable("userId") Long userId, Model model) {
        log.debug("Getting edit user page");
        User user = userService.getFullUserById(userId);
        if (user == null) {
            return "redirect:/404";
        }
        if (user.getUserInfo().isCompany()) {
            if (!user.hasRole(Role.ROLE_AFFILIATE)) {
                user.getRoles().add(Role.ROLE_AFFILIATE);
            }
        }
        user.getUserInfo().setCountryStatePair(CountryCodesUtils.getCountryStatePair(user.getUserInfo().getCountry(), user.getUserInfo().getStateOrProvince()));
        List<PercentInfo> percentInfos = user.getUserInfo().getPercentInfo();
        //Add entry field if compensation type is set and there are no compensation type entries in the database
        if(user.getUserInfo().getPercentType() != PercentType.NONE && percentInfos.size() == 0) {
            percentInfos.add(new PercentInfo());
        }
        model.addAttribute("user", user);
        setNavigation(user, model);
        return "admin/users/user_edit";
    }

    @RequestMapping(value = "/edit/{userId}", method = RequestMethod.POST, params = {"updateUserType"})
    public String updateUserType(@ModelAttribute("user") User user, Model model) {
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        if (user.getUserInfo().isCompany()) {
            if (!user.hasRole(Role.ROLE_AFFILIATE)) {
                user.getRoles().add(Role.ROLE_AFFILIATE);
            }
        }
        if (CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().add(Role.ROLE_USER);
        }
        user.getUserInfo().getPercentInfo().clear();
        user.getUserInfo().setPercentType(PercentType.NONE);
        setNavigation(user, model);
        return "admin/users/user_edit";
    }

    @RequestMapping(value = "/edit/{userId}", method = RequestMethod.POST, params = {"changePercentType"})
    public String changePercentType(@ModelAttribute("user") User user, Model model) {
        List<PercentInfo> percentInfos = user.getUserInfo().getPercentInfo();
        percentInfos.clear();
        if(user.getUserInfo().getPercentType() != PercentType.NONE) {
            percentInfos.add(new PercentInfo());
        }
        setNavigation(user, model);
        return "admin/users/user_edit";
    }

    @RequestMapping(value = "/edit/{userId}", method = RequestMethod.POST, params = {"addRange"})
    public String addRange(@ModelAttribute("user") User user, Model model) {
        user.getUserInfo().getPercentInfo().add(new PercentInfo());
        setNavigation(user, model);
        return "admin/users/user_edit";
    }

    @RequestMapping(value = "/edit/{userId}", method = RequestMethod.POST, params = {"removeRange"})
    public String removeRange(@RequestParam("removeRange") int rangeInd, @ModelAttribute("user") User user, Model model) {
        user.getUserInfo().getPercentInfo().remove(rangeInd);
        setNavigation(user, model);
        return "admin/users/user_edit";
    }

    @RequestMapping(value = "/edit/{userId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String editUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        log.debug("Processing user updateSimpleUser user={}, bindingResult = {}", user, bindingResult);
        if (userService.get(user.getId()) == null) {
            return "redirect:/404";
        }
        userUpdateValidator.validate(user, bindingResult);
        userUpdateValidator.validateRoles(user, bindingResult);
        userInfoValidator.validateWithoutRequired(user.getUserInfo(), bindingResult);
        if (bindingResult.hasErrors()) {
            setNavigation(user, model);
            return "admin/users/user_edit";
        }
        try {
            if (user.getUserInfo().isCompany()) {
                if (!user.hasRole(Role.ROLE_AFFILIATE)) {
                    user.getRoles().add(Role.ROLE_AFFILIATE);
                }
            }
            if (CollectionUtils.isEmpty(user.getRoles())) {
                user.getRoles().add(Role.ROLE_USER);
            }
            List<PercentInfo> percentInfos = user.getUserInfo().getPercentInfo();
            //Remove empty compensation types
            percentInfos.removeIf(pi -> pi.getValue() == null && pi.getValueFrom() == null && pi.getValueTo() == null && pi.getTextValue() == null);
            //Update user
            user = userService.updateWithoutPassword(user);
            //Add entry field if compensation type is set
            percentInfos = user.getUserInfo().getPercentInfo();
            if(user.getUserInfo().getPercentType() != PercentType.NONE && percentInfos.size() == 0) {
                percentInfos.add(new PercentInfo());
            }
            model.addAttribute("user", user);
       } catch (DataIntegrityViolationException e) {
            log.warn("Exception occurred when trying to create the user, assuming duplicate username", e);
            bindingResult.reject("name.exists", "Username already exists");
            setNavigation(user, model);
            return "admin/users/user_edit";
        }
        setNavigation(user, model);
        addMessageSuccess("Saved", model);
        return "admin/users/user_edit";
    }

    @RequestMapping(value = "/edit/password/{userId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    @ResponseBody
    public String updateUserPassword(@PathVariable("userId") Long userId, @RequestParam("password") String password) {
        log.debug("Update password {} for user {}", password, userId);
        if (userId == null) {
            log.error("empty userId");
            return "empty userId";
        }
        if (StringUtils.isBlank(password)) {
            log.error("empty password");
            return "empty password";
        }
        List<String> errors = userCreateValidator.validatePasswordRules(password);
        if (CollectionUtils.isNotEmpty(errors)) {
            return StringUtils.join(errors, ",");
        }
        return userService.updatePassword(userId, password);
    }

    @RequestMapping(value = "registration", method = RequestMethod.GET)
    public String getRegistrationPage(Model model) {
        log.debug("Getting registration user page");
        model.addAttribute("user", new User());
        return "security/registration";
    }

    @RequestMapping(value = "requestNewPassword", method = RequestMethod.GET)
    public String getRestorePasswordPage(Model model) {
        log.debug("Getting restore password page");
        return "security/requestNewPassword";
    }

    @RequestMapping(value = "emailUpdate", method = RequestMethod.GET)
    public String getEmailUpdatePage(Model model) {
        log.debug("Getting email updateSimpleUser page");
        return "security/emailUpdate";
    }

    @RequestMapping(value = "requestNewPassword", method = RequestMethod.POST)
    public String handleRestorePassword(@RequestParam("email") String email, Model model) {
        log.debug("Processing restore password for email={}", email);
        if (SecurityHelper.getCurrentUser() != null) {
            return "redirect:/404";
        }
        User user = userService.getUserByEmail(email);
        if (user != null) {
            userService.restorePassword(user);
            model.addAttribute("message", "Your request has been sent!");
        } else {
            model.addAttribute("message", "Email is not registered");
        }
        return "security/requestNewPassword";
    }

    @RequestMapping(value = "registration", method = RequestMethod.POST)
    public String handleRegistration(@ModelAttribute("user") User user, BindingResult bindingResult,
                                     HttpServletRequest request, HttpServletResponse response,
                                     RedirectAttributes redirectAttributes) {
        log.debug("Processing registration user={}, bindingResult={}", user, bindingResult);
        userCreateValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "security/registration";
        }
        CurrentUser currentUser;
        String password = user.getPassword();
        try {
            User newUser = userService.registration(user);
            currentUser = new CurrentUser(newUser);
            loginService.login(currentUser.getUser().getEmail(), password, request, response);
        } catch (DataIntegrityViolationException e) {
            log.warn("Exception occurred when trying to save the user, assuming duplicate email", e);
            bindingResult.reject("email.exists", "Email already exists");
            return "security/registration";
        }
        redirectAttributes.addFlashAttribute("registrationCompleted", true);
        return "redirect:/";
    }

    @RequestMapping(value = "verification", method = RequestMethod.POST)
    @ResponseBody
    public String handleVerification(@Valid @ModelAttribute User user) {
        userService.verification(user);
        return "Verification email was sent!";
    }

    @RequestMapping(value = "getAdminMenu", method = RequestMethod.POST)
    @Secured({"ROLE_ADMIN", "ROLE_CUSTOMER_SERVICE", "ROLE_ACCOUNTANT", "ROLE_CONTENT_MANAGER", "ROLE_AFFILIATE"})
    public String getAdminMenu() {
        User user = SecurityHelper.getCurrentUser();
        if (user == null) {
            return null;
        }
        if (user.hasRole(Role.ROLE_ADMIN) || user.hasRole(Role.ROLE_CONTENT_MANAGER) || user.hasRole(Role.ROLE_ACCOUNTANT)
                || user.hasRole(Role.ROLE_CUSTOMER_SERVICE) || user.hasRole(Role.ROLE_AFFILIATE)) {
            return "fragments/navbar::adminMenu";
        }
        return null;
    }

    @RequestMapping(value = "emailUpdate", method = RequestMethod.POST)
    public String handleEmailUpdate(@RequestParam("newEmail") String newEmail,
                                    @RequestParam("tokenStr") String tokenStr, Model model) {
        User user = SecurityHelper.getCurrentUser();
        if (user == null) {
            return "redirect:/404";
        }
        VerificationToken token = verificationTokenService.get(tokenStr);
        if (token == null) {
            return "redirect:/404";
        }
        if (token.isVerified()) {
            return "confirmations/confirmationError";
        }
        if (userService.getUserByEmail(newEmail) != null) {
            List<String> errors = new ArrayList<>();
            errors.add("User with this email already exists!");
            model.addAttribute("errors", errors);
            return "security/emailUpdate";
        }
        String oldEmail = user.getEmail();
        User updatedUser = userService.updateUserEmail(user, newEmail);
        if (updatedUser != null) {
            userService.sendUserEmailUpdatedMail(user, oldEmail);
            token.setVerified(true);
            verificationTokenService.saveToken(token);
            return "confirmations/confirmationEmailUpdate";
        }
        model.addAttribute("errorMessage", "Email updateSimpleUser is failed!");
        return "confirmations/confirmationError";
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.GET)
    public String changePassword() {
        User user = SecurityHelper.getCurrentUser();
        if (user == null) {
            return "redirect:/404";
        }
        return "security/restorePassword";
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.POST, params = {"changePassword"})
    public String changePassword(Model model, @RequestParam Map<String, String> requestParameters,
                                 HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        User user = SecurityHelper.getCurrentUser();
        if (user == null) {
            return "redirect:/404";
        }
        String currentPassword = requestParameters.get("currentPassword");
        boolean existedPassword = userService.isPasswordExists(currentPassword, user.getPassword());
        if (!existedPassword) {
            List<String> errors = new ArrayList<>();
            errors.add("Current password is not found");
            model.addAttribute("errors", errors);
            return "security/restorePassword";
        }
        String newPassword = requestParameters.get("newPassword");
        String repeatPassword = requestParameters.get("repeatPassword");
        List<String> errors = userCreateValidator.validatePasswordsMatch(newPassword, repeatPassword);
        errors.addAll(userCreateValidator.validatePasswordRules(newPassword));
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "security/restorePassword";
        }
        User savedUser = userService.saveNewPasswordAndLogin(user.getId(), newPassword, httpServletRequest,
                httpServletResponse);
        if (savedUser != null) {
            userService.sendUpdatePasswordMail(savedUser);
            return "confirmations/confirmationPasswordUpdate";
        } else {
            model.addAttribute("errorMessage", "Password updateSimpleUser is failed!");
            return "confirmations/confirmationError";
        }
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.POST, params = {"updatePassword"})
    public String updatePassword(Model model, @RequestParam Map<String, String> requestParameters, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String tokenStr = requestParameters.get("tokenStr");
        VerificationToken token = verificationTokenService.get(tokenStr);
        if (token == null) {
            return "redirect:/404";
        }
        if (token.isVerified()) {
            return "confirmations/confirmationError";
        }
        String newPassword = requestParameters.get("newPassword");
        String repeatPassword = requestParameters.get("repeatPassword");
        List<String> errors = userCreateValidator.validatePasswordsMatch(newPassword, repeatPassword);
        errors.addAll(userCreateValidator.validatePasswordRules(newPassword));
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("tokenStr", tokenStr);
            return "security/restorePassword";
        }
        User user = token.getUser();
        userService.saveNewPasswordAndLogin(user.getId(), newPassword, httpServletRequest, httpServletResponse);
        userService.sendUpdatePasswordMail(user);
        token.setVerified(true);
        verificationTokenService.saveToken(token);
        return "redirect:/";
    }

    @RequestMapping("/affiliationRequests")
    @Secured("ROLE_ADMIN")
    public String getAffiliationRequests(Model model) {
        log.debug("Getting affiliation requests");
        model.addAttribute("users", userService.getAffiliationRequestingUsers());
        return "admin/users/affiliation_requests";
    }

    @RequestMapping(value = "/approveAffiliationRequest/{userId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String approveAffiliationRequest(@PathVariable("userId") Long userId) {
        log.debug("Approve affiliation request for user id = {} action", userId);
        userService.approveAffiliationRequest(userId);
        return "redirect:/users";
    }

    @RequestMapping(value = "/removeAffiliationRequest/{userId}", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public String removeAffiliationRequest(@PathVariable("userId") Long userId) {
        log.debug("Remove affiliation request for user id = {} action", userId);
        userService.removeAffiliationRequest(userId);
        return "redirect:/users";
    }
}