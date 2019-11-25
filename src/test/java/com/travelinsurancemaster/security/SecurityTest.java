package com.travelinsurancemaster.security;

import com.travelinsurancemaster.InsuranceMasterApp;
import com.travelinsurancemaster.services.security.LoginService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Chernov Artur on 20.04.15.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
@WebAppConfiguration
public class SecurityTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilter;

    @Autowired
    public LoginService loginService;

    private MockMvc mockMvc;

    private final String SECURED_URI = "/users";

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(wac)
                // Enable Spring Security
                .addFilters(springSecurityFilter)
                .alwaysDo(print()).build();
    }

    @Test
    public void itShouldDenyAnonymousAccess() throws Exception {
        mockMvc.perform(get(SECURED_URI))
                .andExpect(status().is(302));
    }

    @Test
    public void itShouldAllowAccessToSecuredPageForPermittedUser() throws Exception {
        Authentication authentication = loginService.login("test2@mail.com", "admin");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext);

        mockMvc.perform(get(SECURED_URI).session(session).principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    public void itShouldDenyAccessToSecuredPageForUserWithoutPermission() throws Exception {
        Authentication authentication = loginService.login("test1@mail.com", "user");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext);

        mockMvc.perform(get(SECURED_URI).session(session).principal(authentication))
                .andExpect(status().is(403));
    }

}