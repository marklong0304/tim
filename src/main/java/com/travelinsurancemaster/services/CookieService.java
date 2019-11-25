package com.travelinsurancemaster.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Created by Chernov Artur on 04.08.15.
 */

@Service
public class CookieService {
    private static final Logger log = LoggerFactory.getLogger(CookieService.class);
    private static final int COOKIE_MAX_AGE = 3 * 365 * 24 * 60 * 60;
    private static final int COOKIE_NULL_AGE = 0;
    private static final String UID_COOKIE = "uid";

    public void saveToCookie(String name, String value, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    public void saveToCookie(String name, String value, int maxAge, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public String getFromCookie(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void removeCookie(String name, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setMaxAge(COOKIE_NULL_AGE);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public String saveUidToCookie(HttpServletRequest request, HttpServletResponse response) {
        String uid = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(UID_COOKIE)) {
                    uid = cookie.getValue();
                }
            }
        }
        if (uid == null) {
            uid = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(UID_COOKIE, uid);
            cookie.setMaxAge(COOKIE_MAX_AGE);
            response.addCookie(cookie);
        }
        return uid;
    }

    public String getCookieUid(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(UID_COOKIE)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void removeCookieUid(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(UID_COOKIE)) {
                    cookie.setMaxAge(COOKIE_NULL_AGE);
                    response.addCookie(cookie);
                }
            }
        }
    }
}
