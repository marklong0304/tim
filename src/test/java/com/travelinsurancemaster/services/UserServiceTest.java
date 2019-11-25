package com.travelinsurancemaster.services;

import com.travelinsurancemaster.InsuranceMasterApp;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.security.VerificationToken;
import com.travelinsurancemaster.repository.UserRepository;
import com.travelinsurancemaster.repository.VerificationTokenRepository;
import com.travelinsurancemaster.services.security.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

;
;


/**
 * Created by Chernov Artur on 19.04.15.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
public class UserServiceTest {

    private static final String USER_NAME = "test123";
    private static final String USER_PASSWORD = "password";
    private static final String USER_EMAIL = "test123@mail.com";
    private static final Role USER_ROLE = Role.ROLE_USER;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    private User testUser;

    @Before
    public void setUp() throws Exception {
        testUser = userService.create(getTestUser());
    }

    @After
    public void tearDown() throws Exception {
        if (testUser != null || (testUser = userRepository.findByEmailIgnoreCase(getTestUser().getEmail())) != null) {
            userRepository.delete(testUser.getId());
        }
    }

    @Test
    public void testGetUserById() {
        User user = userService.get(testUser.getId());
        assertNotNull(user);
        user = userService.get(4242424242L);
        assertNull(user);
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userService.getAll();
        assertThat(users.size(), greaterThan(0));
    }

    @Test
    public void testDeleteUser() {
        assertNotNull(testUser);
        assertEquals(testUser.getName(), USER_NAME);
        User testUserDeleted = userService.delete(testUser);
        assertNotNull(testUserDeleted.getDeleted());
        testUser = userService.getUserByEmail(USER_EMAIL);
        assertNull(testUser);
    }

    @Test
    public void testCreateUser() {
        assertNotNull(testUser);
        assertEquals(testUser.getName(), USER_NAME);
        assertEquals(testUser.getEmail(), USER_EMAIL);
        assertTrue(testUser.hasRole(USER_ROLE));
    }

    @Test
    public void testUpdateUser() {
        String nameUpd = "test123UPD";
        String emailUpd = "test1234@mail.com";
        testUser.setName(nameUpd);
        testUser.setEmail(emailUpd);
        User user = userService.updateWithoutPassword(testUser);
        assertNotNull(user);
        assertNotEquals(user.getName(), USER_NAME);
        assertEquals(user.getName(), nameUpd);
        assertEquals(user.getEmail(), emailUpd);
        assertTrue(testUser.hasRole(USER_ROLE));
        for(VerificationToken token : testUser.getVerificationTokens()){
            verificationTokenRepository.delete(token);
        }
    }

    private User getTestUser() {
        User user = new User();
        user.setName(USER_NAME);
        user.setPassword(USER_PASSWORD);
        user.setEmail(USER_EMAIL);
        user.getRoles().add(USER_ROLE);
        return user;
    }

}
