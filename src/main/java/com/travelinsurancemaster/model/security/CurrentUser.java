package com.travelinsurancemaster.model.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 17.04.15.
 */

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPassword(), user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList()));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public Set<Role> getRole() {
        return user.getRoles();
    }

}