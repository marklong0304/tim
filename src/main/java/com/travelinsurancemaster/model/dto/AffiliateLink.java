package com.travelinsurancemaster.model.dto;

import com.travelinsurancemaster.model.security.User;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Chernov Artur on 25.08.15.
 */

@Entity
public class AffiliateLink implements Serializable {
    static final long serialVersionUID = 1L;

    protected static final int DEFAULT_EXPIRY_TIME_IN_MINS = 60 * 24; //24 hours

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 36)
    private String code;

    @Column(nullable = false)
    protected Date expiryDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "affiliate_link_purchaser", joinColumns = @JoinColumn(name = "affiliate_link_id"))
    private List<AffiliateLinkPurchaser> purchasers = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AffiliateLink() {
    }

    public AffiliateLink(User user, int expirationTimeInMinutes) {
        this.user = user;
        this.code = UUID.randomUUID().toString();
        this.expiryDate = calculateExpiryDate(expirationTimeInMinutes);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public List<AffiliateLinkPurchaser> getPurchasers() {
        return purchasers;
    }

    public void setPurchasers(List<AffiliateLinkPurchaser> purchasers) {
        this.purchasers = purchasers;
    }

    protected Date calculateExpiryDate(int expiryTimeInMinutes) {
        DateTime now = new DateTime();
        return now.plusMinutes(expiryTimeInMinutes).toDate();
    }

    public boolean hasExpired() {
        DateTime tokenDate = new DateTime(getExpiryDate());
        return tokenDate.isBeforeNow();
    }
}