package com.travelinsurancemaster.services.security;

import com.travelinsurancemaster.MailConfig;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.AffiliateLink;
import com.travelinsurancemaster.model.dto.Company;
import com.travelinsurancemaster.model.dto.UserInfo;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.security.VerificationToken;
import com.travelinsurancemaster.repository.UserRepository;
import com.travelinsurancemaster.repository.VerificationTokenRepository;
import com.travelinsurancemaster.services.AffiliateLinkService;
import com.travelinsurancemaster.services.CompanyService;
import com.travelinsurancemaster.services.EmailService;
import com.travelinsurancemaster.services.specifications.UsersSpecification;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 17.04.15.
 */

@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static final String CERTIFICATE_GRABBER_EMAIL = "cert_grabber@travelinsurancemaster.com";

    @Value("${affiliateLink.timeToLive.inMinutes}")
    private Integer affiliateLinkDuration;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MailConfig mailConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginService loginService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Transactional(readOnly = true)
    public User getFullUserById(long id) {
        User user = userRepository.findByIdAndDeletedNull(id);
        if (user == null) {
            return null;
        }
        Hibernate.initialize(user.getUserInfo().getPercentInfo());
        return user;
    }

    @Transactional(readOnly = true)
    public User get(long id) {
        log.debug("Getting user={}", id);
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public User getCertGrabberUser() {
        return userRepository.findByEmailIgnoreCase(CERTIFICATE_GRABBER_EMAIL);
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);
        return userRepository.findByEmailIgnoreCaseAndDeletedNull(email);
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        log.debug("Getting all users");
        return userRepository.findByDeletedNull();
    }

    @Transactional(readOnly = true)
    public List<User> getByEmailOrName(String filter) {
        log.debug("Getting users by email or name");
        return userRepository.findAll(UsersSpecification.findByEmailOrName(filter));
    }

    @Transactional(readOnly = true)
    public List<User> getAffiliateByEmailOrName(String filter) {
        log.debug("Getting users by email or name");
        return userRepository.findAll(UsersSpecification.findAffiliateByEmailOrName(filter));
    }

    @Transactional(readOnly = true)
    public List<User> getAffiliationRequestingUsers() {
        log.debug("Getting affiliation requesting users");
        return userRepository.findByDeletedNullAndAffiliationRequestedNotNullAndAffiliationApprovedNullOrderByAffiliationRequestedDesc();
    }

    @Transactional(readOnly = true)
    public List<User> getAffiliates() {
        log.debug("Getting affiliates");
        return userRepository.findByDeletedNullAndAffiliationApprovedNotNull();
    }

    public User delete(User userParam) {
        log.debug("Delete user={}", userParam.getId());
        User user = get(userParam.getId());
        //Change user's email
        int c = 0;
        String newEmail;
        do {
            newEmail = user.getEmail() + "_" + c++;
        } while (userRepository.findByEmailIgnoreCase(newEmail) != null);
        user.setEmail(newEmail);
        //Set user deleted by the currently logged in user
        user.setDeleted(new Date());
        user.setDeletedBy(loginService.getLoggedInUser() != null ? loginService.getLoggedInUser().getEmail() : null);
        return userRepository.saveAndFlush(user);
    }

    public User create(User user) {
        log.debug("Create user={}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.saveAndFlush(user);
    }

    public User updateSimpleUser(User user) {
        User dbUser = userRepository.findByEmailIgnoreCaseAndDeletedNull(user.getEmail());
        dbUser.setName(user.getName());
        UserInfo dbUserInfo = dbUser.getUserInfo();
        dbUserInfo.setLastName(user.getUserInfo().getLastName());
        dbUserInfo.setZipCode(user.getUserInfo().getZipCode());
        dbUserInfo.setStateOrProvince(user.getUserInfo().getStateOrProvince());
        dbUserInfo.setCity(user.getUserInfo().getCity());
        dbUserInfo.setAddress(user.getUserInfo().getAddress());
        dbUserInfo.setCountry(user.getUserInfo().getCountry());
        dbUserInfo.setLastName(user.getUserInfo().getLastName());
        dbUserInfo.setPhone(user.getUserInfo().getPhone());
        dbUserInfo.setTaxId(user.getUserInfo().getTaxId());
        dbUserInfo.setWebsite(user.getUserInfo().getWebsite());
        dbUserInfo.setConfirmNotification(user.getUserInfo().isConfirmNotification());
        dbUser.setCompany(user.getCompany());
        return userRepository.saveAndFlush(dbUser);
    }

    public User addRole(User user, Role role) {
        User dbUser = getUserByEmail(user.getEmail());
        if(dbUser != null) {
            if(!dbUser.hasRole(role)) {
                dbUser.getRoles().add(role);
            }
        }
        return userRepository.save(dbUser);
    }

    public User addRoles(User user, List<Role> roles) {
        User dbUser = getUserByEmail(user.getEmail());
        if(dbUser != null) {
            roles.forEach(role -> {
                if (!dbUser.hasRole(role)) {
                    dbUser.getRoles().add(role);
                }
            });
        }
        return userRepository.save(dbUser);
    }

    public User updateWithoutPassword(User user) {
        log.debug("Update user={}", user.getEmail());
        User existedUser = get(user.getId());
        if (existedUser == null) {
            return null;
        }
        existedUser.setName(user.getName());
        existedUser.setRoles(user.getRoles());
        existedUser.setCompany(user.getCompany());
        UserInfo existedUserInfo = existedUser.getUserInfo();
        UserInfo userInfo = user.getUserInfo();
        existedUserInfo.setType(userInfo.getType());
        existedUserInfo.setCompany(userInfo.isCompany());
        existedUserInfo.setPercentType(userInfo.getPercentType());
        existedUserInfo.setWebsite(userInfo.getWebsite());
        existedUserInfo.getPercentInfo().clear();
        if (userInfo.getPercentType().getId() != PercentType.NONE.getId()) {
            existedUserInfo.getPercentInfo().addAll(userInfo.getPercentInfo());
        }
        existedUserInfo.setLastName(userInfo.getLastName());
        existedUserInfo.setCompanyName(userInfo.getCompanyName());
        existedUserInfo.setAddress(userInfo.getAddress());
        existedUserInfo.setCity(userInfo.getCity());
        existedUserInfo.setStateOrProvince(userInfo.getStateOrProvince());
        existedUserInfo.setCountry(userInfo.getCountry());
        existedUserInfo.setZipCode(userInfo.getZipCode());
        existedUserInfo.setTaxId(userInfo.getTaxId());
        existedUserInfo.setPhone(userInfo.getPhone());
        if (!existedUser.getEmail().equals(user.getEmail())) {
            existedUser.setVerified(false);
            existedUser.setEmail(user.getEmail());
            sendUserEmailUpdatedMail(user);
        }
        return userRepository.saveAndFlush(existedUser);
    }

    public User approveAffiliationRequest(Long userId) {
        if(userId == null) {
            log.error("Parameter userId is null in approveAffiliationRequest");
            return null;
        }
        User user = userRepository.findByIdAndDeletedNull(userId);
        if (user == null) {
            log.error("No user found with id {} in approveAffiliationRequest", userId);
            return null;
        }
        user.setAffiliationApproved(new Date());
        user.getRoles().add(Role.ROLE_AFFILIATE);
        return userRepository.saveAndFlush(user);
    }

    public User removeAffiliationRequest(Long userId) {
        if(userId == null) {
            log.error("Parameter userId is null in removeAffiliationRequest");
            return null;
        }
        User user = userRepository.findByIdAndDeletedNull(userId);
        if (user == null) {
            log.error("No user found with id {} in removeAffiliationRequest", userId);
            return null;
        }
        user.setAffiliationRequested(null);
        return userRepository.saveAndFlush(user);
    }

    public User setAffiliationNotified(Long userId) {
        if(userId == null) {
            log.error("Parameter userId is null in setAffiliationNotified");
            return null;
        }
        User user = userRepository.findByIdAndDeletedNull(userId);
        if (user == null) {
            log.error("No user found with id {} in setAffiliationNotified", userId);
            return null;
        }
        user.setAffiliationNotified(new Date());
        return userRepository.saveAndFlush(user);
    }

    public String updatePassword(Long userId, String password) {
        if (userId == null) {
            log.error("empty userId");
            return "empty userId";
        }
        User u = userRepository.findByIdAndDeletedNull(userId);
        if (u == null) {
            log.error("no user with id {}", userId);
            return "no user with id " + userId;
        }
        if (StringUtils.isBlank(password)) {
            return "empty password";
        }
        u.setPassword(passwordEncoder.encode(password));
        userRepository.save(u);
        return "success";
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void sendAutoRegistrationEmail(User user, String password) {
        Context ctx = getVerificationContext(user, user.getActiveEmailRegistrationToken());
        ctx.setVariable("userEmail", user.getEmail());
        ctx.setVariable("userPassword", password);
        ctx.setVariable("button4", "cid:logo_button4");
        final String htmlContent = emailService.getHtmlContent(EmailService.AUTO_REGISTRATION_MAIL, ctx);
        emailService.sendRegistrationMail(user.getEmail(), "Auto registration email", htmlContent);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void sendRegistrationEmail(User user, VerificationToken token, String emailSubject) {
        Context ctx = getVerificationContext(user, token);
        ctx.setVariable("button4", "cid:logo_button4");
        final String htmlContent = emailService.getHtmlContent(EmailService.REGISTRATION_MAIL, ctx);
        emailService.sendRegistrationMail(user.getEmail(), emailSubject, htmlContent);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void sendVerificationEmail(User user, VerificationToken token, String emailSubject) {
        Context ctx = getVerificationContext(user, token);
        ctx.setVariable("button4", "cid:logo_button4");
        final String htmlContent = emailService.getHtmlContent(EmailService.VERIFICATION_MAIL, ctx);
        emailService.sendVerificationMail(user.getEmail(), emailSubject, htmlContent);
    }

    private Context getVerificationContext(User user, VerificationToken token) {
        final Context ctx = new Context();
        if (user.getUserInfo() != null && StringUtils.isNotBlank(user.getUserInfo().getLastName())) {
            ctx.setVariable("userName", user.getName() + " " + user.getUserInfo().getLastName());
        } else {
            ctx.setVariable("userName", user.getName());
        }
        String emailVerificationUrl = mailConfig.getSiteAddress() + mailConfig.getTokenVerificationUrl();
        emailVerificationUrl = emailVerificationUrl.concat("/emailRegistration/").concat(token.getToken());
        ctx.setVariable("emailVerificationUrl", emailVerificationUrl);

        ctx.setVariable("logo", "cid:logo_travelinsurancemaster");
        ctx.setVariable("siteAddress", mailConfig.getSiteAddress());

        return ctx;
    }

    /**
     * Auto create user for unauthorized when purchase
     */
    public User autoCreateUser(String email, String name, String lastName,
                               String address, CountryCode country, StateCode stateCode, String city, String zipCode, String phone,
                               HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        user.setEmail(email);
        if (StringUtils.isBlank(name)) {
            fillUserNameFromEmail(user);
        } else {
            user.setName(name);
        }
        user.getUserInfo().setLastName(lastName);
        user.getUserInfo().setAddress(address);
        user.getUserInfo().setCountry(country);
        user.getUserInfo().setStateOrProvince(stateCode);
        user.getUserInfo().setCity(city);
        user.getUserInfo().setZipCode(zipCode);
        user.getUserInfo().setPhone(phone);
        String password = generatePassword();
        user.setPassword(password);
        user.getRoles().add(Role.ROLE_USER);
        VerificationToken token = new VerificationToken(user, VerificationToken.VerificationTokenType.emailRegistration, mailConfig.getEmailRegistrationTokenExpiryTimeInMinutes());
        user.addVerificationToken(token);
        sendAutoRegistrationEmail(user, password);
        user = create(user);
        loginService.login(user.getEmail(), password, request, response);
        return user;
    }

    /**
     * Auto create user for a company
     */
    public User prepareUser(Company company) {
        User user = new User();
        user.getRoles().add(Role.ROLE_USER);
        user.getRoles().add(Role.ROLE_AFFILIATE);
        user.getRoles().add(Role.ROLE_COMPANY_MANAGER);
        return user;
    }

    public User createPreparedUser(User user) {
        VerificationToken token = new VerificationToken(user, VerificationToken.VerificationTokenType.emailRegistration, mailConfig.getEmailRegistrationTokenExpiryTimeInMinutes());
        user.addVerificationToken(token);
        sendRegistrationEmail(user, token, "TIM Registration Email");
        create(user);
        return user;
    }

    private String generatePassword() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    public User registration(User user) {
        return registration(user, false);
    }

    public User requestAffiliation(User user) {
        log.debug("Request affiliation for user = {}", user.getEmail());
        User dbUser = getUserByEmail(user.getEmail());
        dbUser.setAffiliationRequested(new Date());
        userRepository.saveAndFlush(dbUser);
        return dbUser;
    }

    public User registration(User user, boolean byAdmin) {
        if (!byAdmin) {
            user.getRoles().add(Role.ROLE_USER);
        }
        VerificationToken token = new VerificationToken(user, VerificationToken.VerificationTokenType.emailRegistration, mailConfig.getEmailRegistrationTokenExpiryTimeInMinutes());
        user.addVerificationToken(token);
        sendRegistrationEmail(user, token, "TIM Registration Email");
        return create(user);
    }

    public void verification(User user) {
        VerificationToken token = new VerificationToken(user, VerificationToken.VerificationTokenType.emailRegistration, mailConfig.getEmailRegistrationTokenExpiryTimeInMinutes());
        user.addVerificationToken(token);
        tokenRepository.save(token);
        sendVerificationEmail(user, token, "TIM Verification Email");
    }

    public void sendUpdateUserEmail(User user) {
        VerificationToken token = new VerificationToken(user, VerificationToken.VerificationTokenType.emailUpdate,
                mailConfig.getTokenUpdateEmailDuration());
        user.addVerificationToken(token);
        tokenRepository.save(token);
        sendUpdateEmail(user, token.getToken(), "TIM Verification Email");
    }

    private void sendUpdateEmail(User user, String token, String emailSubject) {
        final Context ctx = new Context();
        String updateEmailUrl = mailConfig.getSiteAddress() + mailConfig.getTokenUpdateEmailUrl() + "/" + token;
        if (user.getUserInfo() != null && StringUtils.isNotBlank(user.getUserInfo().getLastName())) {
            ctx.setVariable("userName", user.getName() + " " + user.getUserInfo().getLastName());
        } else {
            ctx.setVariable("userName", user.getName());
        }
        ctx.setVariable("updateEmailUrl", updateEmailUrl);
        ctx.setVariable("button2", "cid:logo_button2");
        final String htmlContent = emailService.getHtmlContent(EmailService.SEND_UPDATE_MAIL, ctx);
        emailService.sendUpdateEmailMail(user.getEmail(), "TIM Update Email", htmlContent);
    }

    public void restorePassword(User user) {
        VerificationToken token = new VerificationToken(user, VerificationToken.VerificationTokenType.lostPassword, mailConfig.getLostPasswordTokenExpiryTimeInMinutes());
        user.addVerificationToken(token);
        tokenRepository.save(token);
        sendRestorePasswordEmail(user, token.getToken());
    }

    private void sendRestorePasswordEmail(User user, String token) {
        final Context ctx = new Context();
        String restorePasswordUrl = mailConfig.getSiteAddress() + mailConfig.getTokenRestorePasswordUrl();
        restorePasswordUrl = restorePasswordUrl.concat("/").concat(token);
        ctx.setVariable("restorePasswordUrl", restorePasswordUrl);
        if (user.getUserInfo() != null && StringUtils.isNotBlank(user.getUserInfo().getLastName())) {
            ctx.setVariable("userName", user.getName() + " " + user.getUserInfo().getLastName());
        } else {
            ctx.setVariable("userName", user.getName());
        }
        ctx.setVariable("button3", "cid:logo_button3");
        final String htmlContent = emailService.getHtmlContent(EmailService.RESTORE_PASSWORD_MAIL, ctx);
        emailService.sendRestoreMail(user.getEmail(), "TIM Restore Password", htmlContent);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void sendUpdatePasswordMail(User user) {
        final Context ctx = new Context();
        if (user.getUserInfo() != null && StringUtils.isNotBlank(user.getUserInfo().getLastName())) {
            ctx.setVariable("userName", user.getName() + " " + user.getUserInfo().getLastName());
        } else {
            ctx.setVariable("userName", user.getName());
        }
        ctx.setVariable("loginUrl", mailConfig.getSiteAddress());
        ctx.setVariable("button1", "cid:logo_button1");
        final String htmlContent = emailService.getHtmlContent(EmailService.PASSWORD_UPDATE_MAIL, ctx);
        emailService.sendLoginMail(user.getEmail(), "TIM Update Password", htmlContent);
    }

    public void sendUserEmailUpdatedMail(User user) {
        sendUserEmailUpdatedMail(user, null);
    }

    public void sendUserEmailUpdatedMail(User user, String oldEmail) {
        final Context ctx = new Context();
        if (user.getUserInfo() != null && StringUtils.isNotBlank(user.getUserInfo().getLastName())) {
            ctx.setVariable("userName", user.getName() + " " + user.getUserInfo().getLastName());
        } else {
            ctx.setVariable("userName", user.getName());
        }
        ctx.setVariable("loginUrl", mailConfig.getSiteAddress());
        ctx.setVariable("button1", "cid:logo_button1");
        final String htmlContent = emailService.getHtmlContent(EmailService.UPDATE_MAIL, ctx);
        if (oldEmail == null) {
            emailService.sendLoginMail(user.getEmail(), "TIM Update Email", htmlContent);
        } else {
            emailService.sendLoginMail(oldEmail, "TIM Update Email", htmlContent);
            verification(user);
        }
    }

    public boolean isPasswordExists(String currentPassword, String encodedPassword) {
        log.debug("Checking if password exists in database");
        return passwordEncoder.matches(currentPassword, encodedPassword);
    }

    public User saveNewPasswordAndLogin(Long id, String newPassword, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if (id == null) {
            return null;
        }
        User user = userRepository.findByIdAndDeletedNull(id);
        if (user == null) {
            return null;
        }
        log.debug("Encoding password for user {}", user.getName());
        user.setPassword(passwordEncoder.encode(newPassword));
        log.debug("Automatic login for user {}", user.getName());
        loginService.login(user.getEmail(), newPassword, httpServletRequest, httpServletResponse);
        log.debug("Saving user {} to database", user.getName());
        return userRepository.save(user);
    }

    public String generateAffiliateLink(User user) {
        List<AffiliateLink> affiliateLinks =  user.getAffiliateLinks().stream().filter(al -> !al.hasExpired()).collect(Collectors.toList());
        int affiliateLinksSize = affiliateLinks.size();
        AffiliateLink affiliateLink = affiliateLinksSize > 0 ? affiliateLinks.get(affiliateLinksSize - 1) : null;
        if(affiliateLink == null) {
            affiliateLink = new AffiliateLink(user, affiliateLinkDuration);
            user.getAffiliateLinks().add(affiliateLink);
            userRepository.save(user);
        }
        String affiliateLinkUrl = mailConfig.getSiteAddress() + AffiliateLinkService.AFFILIATE_LINK_URL + "?" + affiliateLink.getCode();
        return affiliateLinkUrl;
    }

    public String generateAffiliateLink(Long userId) {
        return generateAffiliateLink(get(userId));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public User fillUserNameFromEmail(User user) {
        user.setName(user.getEmail().substring(0, user.getEmail().indexOf('@')));
        return user;
    }

    public User updateUserEmail(User user, String newEmail) {
        if (user == null || newEmail == null) {
            return null;
        }
        user.setEmail(newEmail);
        user.setVerified(false);
        return userRepository.save(user);
    }
}