package com.travelinsurancemaster.model.dto;

import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.util.JsonUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Chernov Artur on 06.05.15.
 */

@Entity
public class QuoteStorage implements Serializable {
    static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, updatable = false, unique = true)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @NotEmpty
    @Column(nullable = false, length = 20000)
    private String quoteRequestJson;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private User affiliate;

    @Column
    private String uid;

    @Column
    private boolean saved;

    @Column
    private boolean deleted;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private QuoteType type = QuoteType.RESULTS;

    @Column
    private String policyUniqueCode;

    @Transient
    private QuoteRequest quoteRequest;

    @Column(nullable = false)
    private boolean original = false;

    @Column(nullable = false)
    private boolean systemSaved;

    public QuoteStorage() {
    }

    public QuoteStorage(QuoteStorage quoteStorage) {
        this.quoteRequestJson = quoteStorage.quoteRequestJson;
        this.affiliate = quoteStorage.affiliate;
        this.createDate = quoteStorage.createDate;
        this.deleted = quoteStorage.deleted;
        this.original = quoteStorage.original;
        this.policyUniqueCode = quoteStorage.policyUniqueCode;
        this.quoteRequest = quoteStorage.quoteRequest;
        this.saved = quoteStorage.saved;
        this.type = quoteStorage.type;
        this.uid = quoteStorage.uid;
        this.user = quoteStorage.user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuoteRequestJson() {
        return quoteRequestJson;
    }

    public void setQuoteRequestJson(String quoteRequestJson) {
        this.quoteRequestJson = quoteRequestJson;
        this.quoteRequest = null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public User getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(User affiliate) {
        this.affiliate = affiliate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public QuoteRequest getQuoteRequestObj() {
        if (quoteRequest == null) {
            quoteRequest = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        }
        return quoteRequest;
    }

    public QuoteType getType() {
        return type;
    }

    public void setType(QuoteType type) {
        this.type = type;
    }

    public String getPolicyUniqueCode() {
        return policyUniqueCode;
    }

    public void setPolicyUniqueCode(String policyUniqueCode) {
        this.policyUniqueCode = policyUniqueCode;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public boolean isSystemSaved() {
        return systemSaved;
    }

    public void setSystemSaved(boolean systemSaved) {
        this.systemSaved = systemSaved;
    }

    public static enum QuoteType {
        RESULTS, DETAILS;
    }
}
