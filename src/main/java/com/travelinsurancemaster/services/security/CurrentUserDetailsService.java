package com.travelinsurancemaster.services.security;

import com.travelinsurancemaster.model.security.CurrentUser;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by Chernov Artur on 17.04.15.
 */
@Service
public class CurrentUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CurrentUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Authenticating user with name={}", username);
        User user = userRepository.findByEmailIgnoreCaseAndDeletedNull(username);
        if (user == null) {
            throw new UsernameNotFoundException("no user");
        }
        return new CurrentUser(user);
    }
}