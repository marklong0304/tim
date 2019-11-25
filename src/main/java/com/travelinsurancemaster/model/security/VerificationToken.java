package com.travelinsurancemaster.model.security;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Chernov Artur on 12.05.15.
 */

@Entity
public class VerificationToken implements Serializable {
    static final long serialVersionUID = 1L;
    private static final int DEFAULT_EXPIRY_TIME_IN_MINS = 60 * 24; //24 hours

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 36)
    private String token;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationTokenType tokenType;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private Date expiryDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public VerificationToken() {
        this.expiryDate = calculateExpiryDate(DEFAULT_EXPIRY_TIME_IN_MINS);
        this.token = UUID.randomUUID().toString();
        this.tokenType = VerificationTokenType.emailRegistration;
    }

    public VerificationToken(User user, VerificationTokenType tokenType, int expirationTimeInMinutes) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate(expirationTimeInMinutes);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public VerificationTokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(VerificationTokenType tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public enum VerificationTokenType {

        lostPassword, emailVerification, emailRegistration, emailUpdate
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        DateTime now = new DateTime();
        return now.plusMinutes(expiryTimeInMinutes).toDate();
    }

    public boolean hasExpired() {
        DateTime tokenDate = new DateTime(getExpiryDate());
        return tokenDate.isBeforeNow();
    }
}
