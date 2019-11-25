package com.travelinsurancemaster.model.dto.purchase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.PurchaseRequest;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.util.JsonUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Chernov Artur on 07.05.15.
 */

@Entity
public class Purchase implements Serializable {
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String purchaseUuid;

    @NotEmpty(message = "Policy number is required!")
    @Column(nullable = false)
    private String policyNumber;

    @Column
    private String orderRequestId;

    @Column
    @Embedded
    @Valid
    private PurchaseQuoteRequest purchaseQuoteRequest;

    @NotNull
    @Column
    private Long timezoneOffset = (long) 0;

    @NotNull(message = "Purchase date is required!")
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Column(nullable = false)
    private LocalDate purchaseDate;

    @ManyToOne
    @JoinColumn(name = "policy_meta_id")
    private PolicyMeta policyMeta;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull(message = "Total price is required!")
    @Column(nullable = false)
    private BigDecimal totalPrice;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "affiliate_commission_id")
    private AffiliateCommission affiliateCommission;

    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vendor_commission_id")
    private VendorCommission vendorCommission;

    @Column(nullable = false, length = 20000)
    private String quoteRequestJson;

    @Column(nullable = false, length = 20000)
    private String purchaseRequestJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_storage_id")
    private QuoteStorage quoteStorage;

    @Column
    private LocalDate cancelled;

    @Column
    private String cancelledBy;

    @Column(nullable = false)
    private boolean success = true;

    @Column(length = 4000)
    private String errorMsg;

    @Column(length = 1000)
    private String note;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PurchaseType purchaseType = PurchaseType.REAL;

    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "purchase_params", joinColumns = @JoinColumn(name = "purchase_id"))
    private Map<String, String> purchaseParams = new HashMap<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @OneToOne(mappedBy = "purchase", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PurchaseResult purchaseResult;

    @Transient
    private QuoteRequest quoteRequest;

    @Transient
    private PurchaseRequest purchaseRequest;

    @Transient
    private String residentCountryStatePair;

    @Transient
    private boolean canDelete;

    public Purchase() {
        purchaseUuid = UUID.randomUUID().toString();
    }

    public Purchase(Purchase purchase) {
        purchaseUuid = UUID.randomUUID().toString();
        this.policyNumber = purchase.getPolicyNumber();
        this.timezoneOffset = purchase.getTimezoneOffset();
        this.purchaseDate = purchase.getPurchaseDate();
        this.policyMeta = purchase.getPolicyMeta();
        this.user = purchase.getUser();
        this.totalPrice = purchase.getTotalPrice();
        this.cancelled = purchase.getCancelled();
        this.cancelledBy = purchase.getCancelledBy();
        this.success = purchase.isSuccess();
        this.purchaseType = purchase.getPurchaseType();
        this.purchaseQuoteRequest = new PurchaseQuoteRequest(purchase.getPurchaseQuoteRequest());
        if(purchase.getVendorCommission() != null) this.vendorCommission = new VendorCommission(purchase.getVendorCommission(), false);
        if(purchase.getAffiliateCommission() != null) this.affiliateCommission = new AffiliateCommission(purchase.getAffiliateCommission(), false);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimezoneOffset() { return timezoneOffset; }
    public void setTimezoneOffset(Long timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public PolicyMeta getPolicyMeta() {
        return policyMeta;
    }
    public void setPolicyMeta(PolicyMeta policyMeta) {
        this.policyMeta = policyMeta;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getOrderRequestId() {
        return orderRequestId;
    }
    public void setOrderRequestId(String orderRequestId) {
        this.orderRequestId = orderRequestId;
    }

    public AffiliateCommission getAffiliateCommission() {
        return affiliateCommission;
    }
    public void setAffiliateCommission(AffiliateCommission affiliateCommission) {
        this.affiliateCommission = affiliateCommission;
    }

    public VendorCommission getVendorCommission() {
        return vendorCommission;
    }
    public void setVendorCommission(VendorCommission vendorCommission) {
        this.vendorCommission = vendorCommission;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getQuoteRequestJson() {
        return quoteRequestJson;
    }
    public void setQuoteRequestJson(String quoteRequestJson) {
        this.quoteRequestJson = quoteRequestJson;
        this.quoteRequest = null;
    }

    public String getPurchaseUuid() {
        return purchaseUuid;
    }
    public void setPurchaseUuid(String purchaseUuid) {
        this.purchaseUuid = purchaseUuid;
    }

    public String getPurchaseRequestJson() {
        return purchaseRequestJson;
    }
    public void setPurchaseRequestJson(String purchaseRequestJson) {
        this.purchaseRequestJson = purchaseRequestJson;
        this.purchaseRequest = null;
    }

    public QuoteStorage getQuoteStorage() {
        return quoteStorage;
    }
    public void setQuoteStorage(QuoteStorage quoteStorage) {
        this.quoteStorage = quoteStorage;
    }

    public QuoteRequest getQuoteRequest() {
        if (quoteRequest == null) {
            quoteRequest = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        }
        return quoteRequest;
    }

    public PurchaseRequest getPurchaseRequest() {
        if (purchaseRequest == null) {
            purchaseRequest = JsonUtils.getObject(purchaseRequestJson, PurchaseRequest.class);
        }
        return purchaseRequest;
    }
    public void setPurchaseRequest(PurchaseRequest purchaseRequest) {
        purchaseRequest.setCreditCard(null);
        this.purchaseRequest = purchaseRequest;
        this.purchaseRequestJson = JsonUtils.getJsonString(purchaseRequest);
    }

    public PurchaseQuoteRequest getPurchaseQuoteRequest() {
        return purchaseQuoteRequest;
    }
    public void setPurchaseQuoteRequest(PurchaseQuoteRequest purchaseQuoteRequest) {
        this.purchaseQuoteRequest = purchaseQuoteRequest;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public Map<String, String> getPurchaseParams() {
        return purchaseParams;
    }
    public void setPurchaseParams(Map<String, String> purchaseParams) {
        this.purchaseParams = purchaseParams;
    }

    public PurchaseType getPurchaseType() {
        return purchaseType;
    }
    public void setPurchaseType(PurchaseType purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getResidentCountryStatePair() {
        return residentCountryStatePair;
    }
    public void setResidentCountryStatePair(String residentCountryStatePair) {
        this.residentCountryStatePair = residentCountryStatePair;
    }

    public LocalDate getCancelled() { return cancelled; }
    public boolean isCancelled() { return cancelled != null; }
    public void setCancelled(LocalDate cancelled) { this.cancelled = cancelled; }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public PurchaseResult getPurchaseResult() { return purchaseResult; }
    public void setPurchaseResult(PurchaseResult purchaseResult) { this.purchaseResult = purchaseResult; }

    @JsonIgnore
    public String getResultPage() { return purchaseResult != null ? purchaseResult.getPage() : null; }

    public static enum PurchaseType {
        REAL, FAKE;
    }

    public boolean getCanDelete() {
        if(affiliateCommission == null) return true;
        AffiliatePayment affiliatePayment = affiliateCommission.getAffiliatePayment();
        if(affiliatePayment == null) return true;
        return affiliatePayment.getStatusPaid() == null;
    }
}