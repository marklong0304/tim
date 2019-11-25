package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.security.VerificationToken;
import com.travelinsurancemaster.repository.UserRepository;
import com.travelinsurancemaster.repository.VerificationTokenRepository;
import com.travelinsurancemaster.util.SecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;


/**
 * Created by Chernov Artur on 12.05.15.
 */

@Service
@Transactional
public class VerificationTokenService {

    private static final Logger log = LoggerFactory.getLogger(VerificationTokenService.class);

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    public VerificationToken get(String token){
        return tokenRepository.findByToken(token);
    }

    /**
     * Email verification via token
     */
    public boolean verifyEmail(String tokenStr, Model model) {
        VerificationToken token = tokenRepository.findByToken(tokenStr);
        if (token == null) {
            log.debug("no token " + tokenStr);
            return false;
        }
        if (token.getTokenType() != VerificationToken.VerificationTokenType.emailRegistration) {
            log.debug("wrong token type");
            return false;
        }
        if (token.isVerified() || token.getUser().isVerified()) {
            log.debug("Token already verified");
            model.addAttribute("errorMessage", "Your email has already been confirmed!");
            return false;
        }
        token.setVerified(true);
        token.getUser().setVerified(true);
        userRepository.save(token.getUser());
        return true;
    }

    /**
     * Password verification by token
     */
    public boolean verifyLostPassword(String tokenStr) {
        VerificationToken token = tokenRepository.findByToken(tokenStr);
        if (token == null) {
            log.debug("no token " + tokenStr);
            return false;
        }
        if (token.getTokenType() != VerificationToken.VerificationTokenType.lostPassword) {
            log.debug("wrong token type");
            return false;
        }
        User currentUser = SecurityHelper.getCurrentUser();
        if (currentUser != null) {
            log.debug("prevent restored password for auth user");
            return false;
        }
        if (token.isVerified()) {
            log.debug("Token already verified");
            return false;
        }
        return true;
    }

    /**
     * Email update by token
     */
    public boolean verifyUpdateEmail(String tokenStr, Model model) {
        VerificationToken token = tokenRepository.findByToken(tokenStr);
        if (token == null) {
            log.debug("no token " + tokenStr);
            return false;
        }
        if (token.getTokenType() != VerificationToken.VerificationTokenType.emailUpdate) {
            log.debug("wrong token type");
            return false;
        }
        if (token.isVerified()) {
            log.debug("Email already updated");
            model.addAttribute("errorMessage", "Your email has already been updated!");
            return false;
        }
        if (token.hasExpired()) {
            log.debug("Token already expired");
            return false;
        }
        return true;
    }

    public VerificationToken saveToken(VerificationToken token) {
        if (token == null) {
            return null;
        }
        return tokenRepository.save(token);
    }
}
