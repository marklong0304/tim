package com.travelinsurancemaster.services;

import com.google.common.collect.Sets;
import com.travelinsurancemaster.MailConfig;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.purchase.*;
import com.travelinsurancemaster.model.dto.report.CommissionReportFilter;
import com.travelinsurancemaster.model.dto.report.PaymentReportFilter;
import com.travelinsurancemaster.model.dto.report.SalaryReportFilter;
import com.travelinsurancemaster.model.dto.report.sales.SalesRequest;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.repository.PolicyMetaRepository;
import com.travelinsurancemaster.repository.PurchaseRepository;
import com.travelinsurancemaster.repository.QuoteStorageRepository;
import com.travelinsurancemaster.repository.VendorRepository;
import com.travelinsurancemaster.services.cms.CertificateService;
import com.travelinsurancemaster.services.converters.PDFService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.services.specifications.PurchaseSpecification;
import com.travelinsurancemaster.util.CountryCodesUtils;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.util.SecurityHelper;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 07.05.15.
 */

@Service
@Transactional
public class PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private QuoteStorageRepository quoteStorageRepository;

    @Autowired
    private TestCreditCards creditCards;

    @Autowired
    private PolicyMetaRepository policyMetaRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MailConfig mailConfig;

    @Autowired
    private PDFService pdfService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private GeoIpDatabaseService geoIpDatabaseComponent;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private QuoteRequestService quoteRequestService;

    @Autowired
    private AffiliateLinkService affiliateLinkService;

    @Autowired
    private QuoteStorageService quoteStorageService;

    @Value("${com.travelinsurancemaster.debug}")
    private boolean isDebug;

    private static final String DATE_FORMAT = "MM/dd/yyyy";

    public  List<Purchase> getAllByUserId(Long id) {
        return purchaseRepository.findAllByUserId(id);
    }

    public Page<Purchase> getAllSuccessByPaymentFilter(PaymentFilter filter, Pageable pageable) {
        return purchaseRepository.findAll(PurchaseSpecification.doPaymentFilter(filter), pageable);
    }

    public List<Purchase> getAllSuccessByPaymentFilter(PaymentFilter filter, Sort sort) {
        return purchaseRepository.findAll(PurchaseSpecification.doPaymentFilter(filter), sort);
    }

    public List<Purchase> getAllSuccessByPaymentFilter(PaymentFilter filter) {
        return purchaseRepository.findAll(PurchaseSpecification.doPaymentFilter(filter));
    }

    public Page<Purchase> getAllSuccessByCommissionFilter(CommissionFilter filter, Pageable pageable) {
        return purchaseRepository.findAll(PurchaseSpecification.doCommissionFilter(filter), pageable);
    }

    public List<Purchase> getAllSuccessByCommissionFilter(CommissionFilter filter, Sort sort) {
        return purchaseRepository.findAll(PurchaseSpecification.doCommissionFilter(filter), sort);
    }

    public Page<Purchase> getAllSuccessByCommissionReportFilter(CommissionReportFilter filter, Pageable pageable) {
        return purchaseRepository.findAll(PurchaseSpecification.doCommissionReportFilter(filter), pageable);
    }

    public List<Purchase> getAllSuccessByCommissionReportFilter(CommissionReportFilter filter, Sort sort) {
        return purchaseRepository.findAll(PurchaseSpecification.doCommissionReportFilter(filter), sort);
    }

    public List<Purchase> getAllSuccessByPaymentReportFilter(PaymentReportFilter filter) {
        return purchaseRepository.findAll(PurchaseSpecification.doPaymentReportFilter(filter));
    }

    public Page<Purchase> getAllSuccessByPaymentReportFilter(PaymentReportFilter filter, Pageable pageable) {
        return purchaseRepository.findAll(PurchaseSpecification.doPaymentReportFilter(filter), pageable);
    }

    public List<Purchase> getAllSuccessByPaymentReportFilter(PaymentReportFilter filter, Sort sort) {
        return purchaseRepository.findAll(PurchaseSpecification.doPaymentReportFilter(filter), sort);
    }

    public List<Purchase> getAllSuccessByCommissionFilter(CommissionFilter filter) {
        return purchaseRepository.findAll(PurchaseSpecification.doCommissionFilter(filter));
    }

    public List<Purchase> getAllSuccessByCommissionReportFilter(CommissionReportFilter filter) {
        return purchaseRepository.findAll(PurchaseSpecification.doCommissionReportFilter(filter));
    }

    public Page<Purchase> getAllSuccessBySalaryFilter(SalaryFilter filter, Pageable pageable) {
        return purchaseRepository.findAll(PurchaseSpecification.doSalaryFilter(filter), pageable);
    }

    public List<Purchase> getAllSuccessBySalaryFilter(SalaryFilter filter, Sort sort) {
        return purchaseRepository.findAll(PurchaseSpecification.doSalaryFilter(filter), sort);
    }

    public Page<Purchase> getAllSuccessBySalaryReportFilter(SalaryReportFilter filter, Pageable pageable) {
        return purchaseRepository.findAll(PurchaseSpecification.doSalaryReportFilter(filter), pageable);
    }

    public List<Purchase> getAllSuccessBySalaryReportFilter(SalaryReportFilter filter, Sort sort) {
        return purchaseRepository.findAll(PurchaseSpecification.doSalaryReportFilter(filter), sort);
    }

    public List<Purchase> getAllSuccessBySalaryFilter(SalaryFilter filter) {
        return purchaseRepository.findAll(PurchaseSpecification.doSalaryFilter(filter));
    }

    public Page<Purchase> getAllSuccessBySalarySearchFilter(SalaryFilter filter, Pageable pageable) {
        return purchaseRepository.findAll(PurchaseSpecification.doSalarySearchFilter(filter), pageable);
    }

    public List<Purchase> getAllSuccessBySalarySearchFilter(SalaryFilter filter) {
        return purchaseRepository.findAll(PurchaseSpecification.doSalarySearchFilter(filter));
    }

    public List<Purchase> getAllSuccessBySalarySearchFilter(SalaryFilter filter, Sort sort) {
        return purchaseRepository.findAll(PurchaseSpecification.doSalarySearchFilter(filter), sort);
    }

    public List<Purchase> getAllSuccessBySalaryReportFilter(SalaryReportFilter filter) {
        return purchaseRepository.findAll(PurchaseSpecification.doSalaryReportFilter(filter));
    }

    public List<Purchase> getAllSuccessBySalesRequest(SalesRequest request) {
        return purchaseRepository.findAll(PurchaseSpecification.doSalesFilter(request));
    }

    public Purchase save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    public Purchase getPurchase(Long id) {
        return purchaseRepository.findOne(id);
    }

    public Purchase getPurchase(String uuid) {
        return purchaseRepository.findByPurchaseUuid(uuid);
    }

    public List<Purchase> getByPolicyNumber(String policyNumber, String vendor) {
        return purchaseRepository.findByPolicyNumberAndPolicyMetaVendorCode(policyNumber, vendor);
    }

    public List<Purchase> getByPolicyNumberAndVendorName(String policyNumber, String vendor) {
        return purchaseRepository.findByPolicyNumberAndPolicyMetaVendorName(policyNumber, vendor);
    }

    public List<Purchase> getTravellersOfSuccessPurchase(String query) {
        return purchaseRepository.findAll(PurchaseSpecification.getTravellersOfSuccessPurchase(query));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Purchase savePurchase(
            PurchaseRequest request,
            PurchaseResponse response,
            User userParam,
            String quoteId,
            Optional<AffiliateLink> affiliateLink,
            String errorMsg,
            HttpServletRequest httpServletRequest,
            Long timezoneOffset) {

        if (request == null || userParam == null || StringUtils.isBlank(quoteId)) {
            throw new NullPointerException("wrong savePurchase params " + request + ", " + userParam + ", " + quoteId);
        }
        User user = userService.get(userParam.getId());
        Purchase purchase = new Purchase();
        purchase.setTimezoneOffset(timezoneOffset);
        purchase.setPurchaseDate(DateUtil.getLocalDateNow(timezoneOffset));
        PolicyMeta policyMeta = policyMetaRepository.findByUniqueCodeAndDeletedDateIsNull(request.getProduct().getPolicyMeta().getUniqueCode());
        purchase.setPolicyMeta(policyMeta);
        purchase.setUser(user);
        purchase.setTotalPrice(request.getProduct().getTotalPrice());

        QuoteStorage quoteStorage = quoteStorageRepository.findOne(quoteId);
        QuoteRequest quoteRequest = quoteStorage.getQuoteRequestObj();
        quoteRequest.getTravelers().clear();
        quoteRequest.getTravelers().addAll(request.getTravelers());
        String quoteRequestJsonUpdated = JsonUtils.getJsonString(quoteRequest);

        purchase.setQuoteStorage(quoteStorage);
        purchase.setQuoteRequestJson(quoteRequestJsonUpdated);
        purchase.setPurchaseRequest(request);

        if (response != null && response.getStatus() == Result.Status.SUCCESS) {
            purchase.setPolicyNumber(response.getPolicyNumber());
            purchase.setOrderRequestId(response.getOrderRequestId());
            purchase.setPurchaseParams(response.getParams());
            commissionService.fillAffiliateCommission(quoteStorage, user, purchase, affiliateLink);
            commissionService.fillVendorCommission(purchase);
            if(affiliateLink.isPresent() && affiliateLinkService.isAffiliateLinkEligible(user, affiliateLink.get())) {
                userService.approveAffiliationRequest(affiliateLink.get().getUser().getId());
            }
        } else {
            purchase.setPolicyNumber("-1");
            purchase.setSuccess(false);
            if (response != null) {
                purchase.setErrorMsg(response.getErrorMsg());
            } else {
                purchase.setErrorMsg(errorMsg);
            }
        }
        purchase.setPurchaseQuoteRequest(new PurchaseQuoteRequest(quoteRequest));

        PurchaseResult purchaseResult = new PurchaseResult();
        purchaseResult.setPage(response.getResultPage());
        purchaseResult.setPurchase(purchase);
        purchase.setPurchaseResult(purchaseResult);

        Purchase newPurchase = save(purchase);

        if (newPurchase != null && newPurchase.isSuccess() && httpServletRequest != null) {
            sendPurchaseEmail(newPurchase, httpServletRequest);
        }
        return newPurchase;
    }

    /**
     * Fill purchaseRequest before purchasePage is displayed.
     */
    public PurchaseRequest fillPurchaseRequest(QuoteRequest quoteRequest, Product product, HttpServletRequest httpServletRequest) {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setQuoteRequest(quoteRequest);
        purchaseRequest.setProduct(product);
        User user = SecurityHelper.getCurrentUser();
        if (user != null) {
            user = userService.get(user.getId());
        }
        int i = 0;
        for (GenericTraveler quoteTraveler : quoteRequest.getTravelers()) {
            GenericTraveler traveler = new GenericTraveler(quoteTraveler.getAgeOrBirthday());
            purchaseRequest.getTravelers().add(traveler);
            traveler.setPrimary(i++ == 0);
        }
        if (user != null && user.getUserInfo() != null) {
            purchaseRequest.setEmail(user.getEmail());
        } else {
            geoIpDatabaseComponent.fillUserPurchaseInfoByIP(purchaseRequest, httpServletRequest);
        }

        if (isDebug) {
            CreditCard creditCard = creditCards.getCreditCardByVendorCode(product.getPolicyMeta().getVendor().getCode());
            if (creditCard != null) {
                purchaseRequest.setCreditCard(creditCard);
                purchaseRequest.setCreditCardCountryStatePair(CountryCodesUtils.getCountryStatePair(creditCard.getCcCountry(), creditCard.getCcStateCode()));
                purchaseRequest.setPostalCode("55959");
                purchaseRequest.setCity("Minnesota");
                purchaseRequest.setAddress("Excelsior Blvd. 101");
                purchaseRequest.setEmail("testemail@someemail.com");
                purchaseRequest.setPhone("2185973198");
                purchaseRequest.setTripTypes(Sets.newHashSet(TripType.Air.toString()));
                purchaseRequest.setBillingSameAsHome(true);
                GenericTraveler genericTraveler = purchaseRequest.getTravelers().get(0);
                genericTraveler.setFirstName("Alex");
                genericTraveler.setLastName("Test");
                genericTraveler.setBirthday(LocalDate.now().minusYears(genericTraveler.getAge() + 1).plusMonths(6));
                genericTraveler.setBeneficiary("Some Beneficiary");
                genericTraveler.setBeneficiaryRelation(BeneficiaryRelation.Son);
            }
        }

        return purchaseRequest;
    }

    public PurchaseRequest preparePurchaseRequest(PurchaseRequest purchaseRequest) {
        String phone = purchaseRequest.getPhone().replaceAll("[^0-9]", "");
        purchaseRequest.setPhone(phone);
        populateTravelersTripCost(purchaseRequest.getTravelers(), purchaseRequest.getQuoteRequest().getTravelers());
        quoteRequestService.fillUpsalesBeforePurchase(purchaseRequest.getQuoteRequest());
        return purchaseRequest;
    }

    public Purchase createNewPurchase(String purchaseUuid) {
        //Get purchase to copy
        Purchase purchase = getPurchase(purchaseUuid);
        //Create new purchase from submitted form
        Purchase newPurchase = new Purchase(purchase);
        //Copy quote storage and set it the newly created purchase
        QuoteStorage quoteStorage = purchase.getQuoteStorage();
        QuoteStorage newQuoteStorage = quoteStorageService.saveNewQuote(quoteStorage.getId(), quoteStorage.getQuoteRequestJson(), quoteStorage.getUser());
        newPurchase.setQuoteStorage(newQuoteStorage);
        //Set quote request json in purchase from the copied quote storage
        newPurchase.setQuoteRequestJson(newQuoteStorage.getQuoteRequestJson());
        newPurchase.setPurchaseRequest(purchase.getPurchaseRequest());
        //Adjust policy number in the existing purchase
        purchase.setPolicyNumber("-" + purchase.getPolicyNumber());
        save(purchase);
        return newPurchase;
    }

    public Purchase updatePurchaseFromForm(Purchase purchase, Purchase purchaseForm, Integer primaryTravelerIndex, boolean updateCommissions) {

        PurchaseQuoteRequest purchaseQuoteRequest = purchaseForm.getPurchaseQuoteRequest();
        PurchaseRequest purchaseRequest = purchaseForm.getPurchaseRequest();

        setPrimaryTraveler(purchaseForm, primaryTravelerIndex);

        PurchaseQuoteRequest savedPurchaseQuoteRequest = purchase.getPurchaseQuoteRequest();
        PurchaseRequest savedPurchaseRequest = purchase.getPurchaseRequest();

        //Update fields edited on the form
        savedPurchaseQuoteRequest.setDepartDate(purchaseQuoteRequest.getDepartDate());
        savedPurchaseQuoteRequest.setReturnDate(purchaseQuoteRequest.getReturnDate());
        savedPurchaseQuoteRequest.setTripCost(purchaseQuoteRequest.getTripCost());
        savedPurchaseQuoteRequest.setDepositDate(purchaseQuoteRequest.getDepositDate());
        savedPurchaseQuoteRequest.setPaymentDate(purchaseQuoteRequest.getPaymentDate());
        savedPurchaseQuoteRequest.setDestinationCountry(purchaseQuoteRequest.getDestinationCountry());

        purchase.setPolicyNumber(purchaseForm.getPolicyNumber());
        purchase.setTotalPrice(purchaseForm.getTotalPrice());
        purchase.setPurchaseDate(purchaseForm.getPurchaseDate());

        purchase.setPolicyMeta(purchaseForm.getPolicyMeta());

        //Update primary traveler info
        savedPurchaseQuoteRequest.setResidentCountry(purchaseQuoteRequest.getResidentCountry());
        savedPurchaseQuoteRequest.setResidentState(purchaseQuoteRequest.getResidentState());

        //Update travellers
        cleanNullTravelers(purchaseQuoteRequest);
        savedPurchaseQuoteRequest.setTravelers(purchaseQuoteRequest.getTravelers());

        if(updateCommissions) {
            //Update vendor commission
            VendorCommission vendorCommissionForm = purchaseForm.getVendorCommission();
            if (vendorCommissionForm != null) {
                VendorCommission vendorCommission = purchase.getVendorCommission();
                if(vendorCommissionForm.getReceivedCommission() != null && vendorCommissionForm.getReceivedCommission().compareTo(BigDecimal.ZERO) > 0) {
                    vendorCommission.setReceivedCommission(vendorCommissionForm.getReceivedCommission());
                    vendorCommission.setConfirm(true);
                    if(vendorCommissionForm.getReceivedDate() != null) {
                        vendorCommission.setReceivedDate(vendorCommissionForm.getReceivedDate());
                    } else {
                        vendorCommission.setReceivedDate(java.sql.Date.valueOf(DateUtil.getLocalDateNow(purchase.getTimezoneOffset())));
                    }
                } else {
                    vendorCommission.setReceivedCommission(null);
                    vendorCommission.setConfirm(false);
                    vendorCommission.setReceivedDate(null);
                }
            }
            //Update affiliate commission
            AffiliateCommission affiliateCommissionForm = purchaseForm.getAffiliateCommission();
            if (affiliateCommissionForm != null) {
                purchase.getAffiliateCommission().setSalaryToPay(affiliateCommissionForm.getSalaryToPay() != null ? affiliateCommissionForm.getSalaryToPay() : new BigDecimal(0));
            }
        }

        //Update json field purchase_request
        savedPurchaseRequest.getProduct().setTotalPrice(purchaseForm.getTotalPrice());
        savedPurchaseRequest.setTravelers(prepareGenericTravelers(purchaseQuoteRequest.getTravelers(), savedPurchaseRequest.getTravelers(), purchaseQuoteRequest.getTripCost()));
        savedPurchaseRequest.setCity(purchaseRequest.getCity());
        savedPurchaseRequest.setPostalCode(purchaseRequest.getPostalCode());
        savedPurchaseRequest.setAddress(purchaseRequest.getAddress());
        savedPurchaseRequest.setAddressLine2(purchaseRequest.getAddressLine2());
        savedPurchaseRequest.setPhone(purchaseRequest.getPhone());
        purchase.setPurchaseRequest(savedPurchaseRequest);

        return purchase;
    }

    private PurchaseTraveler setPrimaryTraveler(Purchase purchase, Integer primaryTravelerIndex) {
        PurchaseTraveler primaryTraveler = null;
        PurchaseQuoteRequest purchaseQuoteRequest = purchase.getPurchaseQuoteRequest();
        List<PurchaseTraveler> travelers = purchaseQuoteRequest.getTravelers();
        travelers.forEach(t -> t.setPrimary(false));
        if(primaryTravelerIndex < travelers.size()) {
            primaryTraveler = travelers.get(primaryTravelerIndex);
            primaryTraveler.setPrimary(true);
            purchaseQuoteRequest.setPrimaryTraveler(primaryTraveler);
        }
        return primaryTraveler;
    }

    private void cleanNullTravelers(PurchaseQuoteRequest purchaseQuoteRequest) {
        purchaseQuoteRequest.getTravelers().removeIf(t -> t.getFirstName() == null && t.getLastName() == null);
    }

    private List<GenericTraveler> prepareGenericTravelers(List<PurchaseTraveler> travelers, List<GenericTraveler> genericTravelers, BigDecimal tripCost) {
        List<GenericTraveler> preparedGenericTravelers = new ArrayList<>();
        String beneficiary = null;
        BeneficiaryRelation beneficiaryRelation = null;
        Optional<GenericTraveler> primaryGenericTravelerOptional = genericTravelers.stream().filter(gt -> gt.isPrimary()).findFirst();
        GenericTraveler primaryGenericTraveler = primaryGenericTravelerOptional.isPresent() ? primaryGenericTravelerOptional.get() : null;
        if(primaryGenericTraveler != null) {
            beneficiary = primaryGenericTraveler.getBeneficiary();
            beneficiaryRelation = primaryGenericTraveler.getBeneficiaryRelation();
        }
        for(PurchaseTraveler traveler : travelers) {
            if(traveler.isPrimary()) {
                preparedGenericTravelers.add(new GenericTraveler(traveler, tripCost, beneficiary, beneficiaryRelation));
            } else {
                preparedGenericTravelers.add(new GenericTraveler(traveler, tripCost, null, null));
            }
        }
        return preparedGenericTravelers;
    }

    private void populateTravelersTripCost(List<GenericTraveler> purchaseRequestTravelers, List<GenericTraveler> quoteRequestTravelers) {
        for (GenericTraveler purchaseRequestTraveler : purchaseRequestTravelers) {
            purchaseRequestTraveler.setTripCost(quoteRequestTravelers.get(0).getTripCost());
        }
    }

    /**
     * @return purchased plans list to be displayed on purchased plans page.
     */
    public List<Purchase> getPurchases(User user, String dateStrFrom, String dateStrTo, Long vendorId) {
        if (user == null) {
            return Collections.emptyList();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date dateFrom = null;
        Date dateTo = null;
        if (!StringUtils.isEmpty(dateStrFrom)) {
            try {
                dateFrom = dateFormat.parse(dateStrFrom);
            } catch (ParseException e) {
                log.warn(e.getMessage(), e);
            }
        }
        if (!StringUtils.isEmpty(dateStrTo)) {
            dateTo = DateUtil.getEndOfDay(dateStrTo, DATE_FORMAT);
        }
        Vendor vendor = null;
        if (vendorId != null) {
            vendor = vendorRepository.findOne(vendorId);
        }
        return purchaseRepository.findAll(PurchaseSpecification.checkParameters(user, dateFrom, dateTo, vendor, Purchase.PurchaseType.REAL));
    }

    public List<Purchase> getCurrentPolicies(User user) {
        if (user == null || user.getUserInfo() == null || user.getUserInfo().isCompany()) {
            return Collections.emptyList();
        }
        return purchaseRepository.findCurrentPolicies(user, Purchase.PurchaseType.REAL);
    }

    private void sendPurchaseEmail(Purchase purchase, HttpServletRequest httpServletRequest) {
        Context ctx = getPurchaseContext();
        ctx.setVariable("userName", purchase.getPurchaseQuoteRequest().getPrimaryTraveler().getFirstName());
        ctx.setVariable("quoteRequest", purchase.getQuoteRequest());
        ctx.setVariable("purchaseRequest", purchase.getPurchaseRequest());
        ctx.setVariable("purchase", purchase);
        StringBuilder address = new StringBuilder();
        address.append(purchase.getPurchaseRequest().getAddress()).append(", ");
        if (StringUtils.isNotBlank(purchase.getPurchaseRequest().getCity())) {
            address.append(purchase.getPurchaseRequest().getCity()).append(", ");
        }
        if (purchase.getQuoteRequest().getResidentState() != null) {
            address.append(purchase.getQuoteRequest().getResidentState().getCaption()).append(", ");
        }
        if (purchase.getQuoteRequest().getResidentCountry() != null) {
            address.append(purchase.getQuoteRequest().getResidentCountry().getCaption()).append(" ");
        }
        address.append(purchase.getPurchaseRequest().getPostalCode());
        ctx.setVariable("address", address);
        String certificateLink = certificateService.getCertificateUrl(purchase.getQuoteRequest(), purchase.getPolicyMeta(), httpServletRequest);
        ctx.setVariable("certificateLink", StringUtils.isNotBlank(certificateLink) ? certificateLink : null);
        ctx.setVariable("affiliateLink", userService.generateAffiliateLink(purchase.getUser()));
        ctx.setVariable("reviewsTermsAndConditionsLink", getLinkToPage("Reviews-Terms-and-Conditions"));
        ctx.setVariable("affiliateProgramLink", getLinkToPage("Affiliate-Program"));
        ctx.setVariable("siteLink", mailConfig.getSiteAddress());
        final String htmlContent = emailService.getHtmlContent(EmailService.PURCHASE_MAIL, ctx);
        String accountEmail = purchase.getUser().getEmail();
        emailService.sendMail(accountEmail, "TIM Purchase Policy Confirmation", htmlContent);
        if(!purchase.getUser().isAffiliate() && purchase.getUser().getAffiliationApproved() == null && purchase.getUser().getAffiliationNotified() == null) {
            final String htmlContentReward = emailService.getHtmlContent(EmailService.PURCHASE_REWARD_MAIL, ctx);
            emailService.sendMail(accountEmail, "Easy Bucks - Affiliate With TIM", htmlContentReward);
            userService.setAffiliationNotified(purchase.getUser().getId());
        }
        String customerEmail = purchase.getPurchaseRequest().getEmail();
        if (!accountEmail.equals(customerEmail)) {
            ctx.setVariable("affiliateLink", "");
            final String htmlContentCustomer = emailService.getHtmlContent(EmailService.PURCHASE_MAIL, ctx);
            emailService.sendMail(customerEmail, "TIM Purchase Policy Confirmation", htmlContentCustomer);
        }
    }

    private String getLinkToPage(String pageName) {
        return mailConfig.getSiteAddress() + "/page/" + pageName;
    }

    public void fillTravelersFromJson(Purchase purchase) {
        PurchaseQuoteRequest purchaseQuoteRequest = purchase.getPurchaseQuoteRequest();
        PurchaseRequest jsonPurchaseRequest = purchase.getPurchaseRequest();
        purchaseQuoteRequest.setTravelers(jsonPurchaseRequest.getTravelers().stream().map(t -> new PurchaseTraveler(t)).collect(Collectors.toList()));
    }

    public String getPurchasePdfName(Purchase purchase) {
        List<String> purchaseParams = new ArrayList<>();
        purchaseParams.add(purchase.getPolicyMeta().getVendor().getName());
        purchaseParams.add(purchase.getPolicyMeta().getDisplayName());
        purchaseParams.add(DateFormatUtils.format(DateUtil.fromLocalDate(purchase.getQuoteRequest().getDepartDate()), "MMM_dd_yyyy", Locale.US));
        purchaseParams.add(DateFormatUtils.format(DateUtil.fromLocalDate(purchase.getQuoteRequest().getReturnDate()), "MMM_dd_yyyy", Locale.US));
        purchaseParams.add(purchase.getTotalPrice().toString() + "$");
        return StringUtils.join(purchaseParams, '-').replace(' ', '_') + ".pdf";
    }

    public void sendEndTripEmails() {
        List<Purchase> endTripPolicies = purchaseRepository.findEndTripPolicies(LocalDate.now().minusDays(1), Purchase.PurchaseType.REAL);
        List<MimeMessage> messages = new ArrayList<>(endTripPolicies.size());
        for (Purchase endTripPolicyPurchase : endTripPolicies) {
            MimeMessage mimeMessage = sendEndTripEmails(endTripPolicyPurchase);
            if (mimeMessage != null) {
                messages.add(mimeMessage);
            }
        }
        emailService.sendMimeMessages(messages.toArray(new MimeMessage[messages.size()]));
    }

    private MimeMessage sendEndTripEmails(Purchase purchase) {
        final Context ctx = new Context();
        ctx.setVariable("userName", WordUtils.capitalize(purchase.getUser().getName()));
        final String htmlContent = emailService.getHtmlContent(EmailService.END_TRIP_MAIL, ctx);
        try {
            return emailService.createMimeMessage(purchase.getUser().getEmail(), "Welcome Back", htmlContent, null);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private Context getPurchaseContext() {
        final Context ctx = new Context();
        ctx.setVariable("logo", "cid:logo_travelinsurancemaster");
        ctx.setVariable("siteAddress", mailConfig.getSiteAddress());

        return ctx;
    }

    public void delete(long purchaseId) {
        purchaseRepository.delete(purchaseId);
    }
}