package com.travelinsurancemaster.services.security;

import com.travelinsurancemaster.model.security.CurrentUser;
import com.travelinsurancemaster.model.security.LoginStatus;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.CookieService;
import com.travelinsurancemaster.util.SecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Chernov Artur on 13.05.15.
 */

@Service
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    @Qualifier("tisAuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private RememberMeServices rememberMeServices;

    @Autowired
    private UserService userService;

    public LoginStatus getStatus() {
        User user = SecurityHelper.getCurrentUser();
        if (user != null) {
            return new LoginStatus(true, user.getName(), user.getRoles().iterator().next().name());
        } else {
            return new LoginStatus(false, null, null);
        }
    }

    public LoginStatus login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication auth = login(username, password);
            rememberMeServices.loginSuccess(request, response, auth);
            log.debug("Login succeeded!");
            SecurityContextHolder.getContext().setAuthentication(auth);
            cookieService.removeCookieUid(request, response);
            CurrentUser user = (CurrentUser) auth.getPrincipal();
            return new LoginStatus(auth.isAuthenticated(), user.getUser().getName(), user.getRole().toString());
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage(), e);
            rememberMeServices.loginFail(request, response);
            return new LoginStatus(false, username, null);
        }
    }

    public Authentication login(String username, String password) throws Exception {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(token);
    }

    public User getLoggedInUser() {
        User user = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) {
            user = userService.getUserByEmail(auth.getName());
        }
        return user;
    }
}
