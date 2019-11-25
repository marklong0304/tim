package com.travelinsurancemaster.util;

import com.travelinsurancemaster.model.security.CurrentUser;
import com.travelinsurancemaster.model.security.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by ritchie on 5/13/15.
 */
public class SecurityHelper {

    /**
      * @return authorized user
     */
    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        if (auth.getPrincipal() instanceof CurrentUser) {
            return ((CurrentUser) auth.getPrincipal()).getUser();
        }
        return null;
    }
}
