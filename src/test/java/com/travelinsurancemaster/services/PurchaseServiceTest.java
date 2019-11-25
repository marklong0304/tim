package com.travelinsurancemaster.services;

/**
 * Created by raman on 19.06.19.
 */

import com.travelinsurancemaster.InsuranceMasterApp;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.AffiliateLink;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.QuoteStorage;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertNotNull;

@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
public class PurchaseServiceTest {

    private final LocalDate today = LocalDate.now();

    private final long POLICY_META_ID = 1;
    private final long USER_ID = 2;
    private final long USER_AFFILIATE_ID = 3;

    @Value("${affiliateLink.timeToLive.inMinutes}")
    private Integer affiliateLinkDuration;

    @Autowired
    private QuoteRequestService quoteRequestService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PolicyMetaCodeService policyMetaCodeService;

    @Autowired
    private QuoteStorageService quoteStorageService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private UserService userService;

    @Test
    public void savePurchase() {

        BigDecimal tripCost = new BigDecimal(3000);
        List<GenericTraveler> travelers = new ArrayList<>();
        GenericTraveler traveler = new GenericTraveler();
        traveler.setFirstName("Joe");
        traveler.setLastName("Black");
        traveler.setBirthday(DateUtil.getLocalDate("01/01/1990"));
        traveler.setPrimary(true);
        traveler.setTripCost(tripCost);
        travelers.add(traveler);

        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setTravelers(travelers);
        quoteRequest.setDepartDate(today.plusDays(1));
        quoteRequest.setReturnDate(today.plusDays(11));
        quoteRequest.setTripCost(tripCost);
        quoteRequest.setCitizenCountry(CountryCode.US);
        quoteRequest.setResidentCountry(CountryCode.US);
        quoteRequest.setResidentState(StateCode.GA);
        quoteRequest.setDestinationCountry(CountryCode.DZ);
        quoteRequest.setDepositDate(today.minusDays(2));
        quoteRequest.setPaymentDate(today.minusDays(1));

        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(POLICY_META_ID);
        //Set links to null to prevent infinite recursions
        policyMeta.setVendor(null);
        policyMeta.setPolicyQuoteParams(null);
        policyMeta.setPolicyMetaCategories(null);
        policyMeta.setPolicyMetaPackages(null);
        //Set link to null to prevent attempting lazy initialization
        policyMeta.setPlanTypes(null);

        Product product = new Product();
        product.setPolicyMeta(policyMeta);
        product.setPolicyMetaCode(policyMetaCodeService.getPolicyMetaCode(policyMeta.getId(), quoteRequest));
        product.setTotalPrice(new BigDecimal(100));

        QuoteRequest quoteRequestWithUpsaleForPolicy = policyMetaService.getQuoteRequestWithUpsaleForPolicy(policyMeta, quoteRequest, false);
        QuoteRequest quoteRequestWithCancellation = quoteRequestService.getQuoteRequestWithCancellation(quoteRequestWithUpsaleForPolicy, policyMeta);

        CreditCard creditCard = new CreditCard();
        creditCard.setCcName("Joe Black");
        creditCard.setCcType(CardType.VISA);
        creditCard.setCcNumber(new Long("4111111111111111"));
        creditCard.setCcExpMonth("12");
        creditCard.setCcExpYear("2099");
        creditCard.setCcCode("123");

        PurchaseRequest purchaseRequest = new PurchaseRequest();

        purchaseRequest.setProduct(product);

        purchaseRequest.setCity("Atlanta");
        purchaseRequest.setAddress("123 Test Dr");
        purchaseRequest.setPostalCode("30303");
        purchaseRequest.setPhone("123-456-7890");
        purchaseRequest.setEmail("test@test.com");

        purchaseRequest.setCreditCard(creditCard);
        purchaseRequest.setTravelers(travelers);

        purchaseRequest.setQuoteRequest(quoteRequestWithCancellation);
        purchaseRequest.copyBillingFromHome();
        purchaseRequest = purchaseService.preparePurchaseRequest(purchaseRequest);

        PurchaseResponse purchaseResponse = new PurchaseResponse();
        purchaseResponse.setStatus(Result.Status.SUCCESS);
        purchaseResponse.setPolicyNumber("-1");

        User user = userService.getFullUserById(USER_ID);
        User affiliate = userService.getFullUserById(USER_AFFILIATE_ID);

        AffiliateLink affiliateLink = new AffiliateLink(affiliate, affiliateLinkDuration);

        QuoteStorage quoteStorage = new QuoteStorage();
        quoteStorage.setQuoteRequestJson(JsonUtils.getJsonString(quoteRequest));
        quoteStorage.setUid(UUID.randomUUID().toString());
        quoteStorage.setCreateDate(new Date());
        quoteStorage.setUser(user);
        quoteStorage.setAffiliate(affiliate);
        if (policyMeta != null) {
            quoteStorage.setType(QuoteStorage.QuoteType.DETAILS);
            quoteStorage.setPolicyUniqueCode(policyMeta.getUniqueCode());
        }
        quoteStorage = quoteStorageService.save(quoteStorage);

        Purchase purchase = purchaseService.savePurchase(purchaseRequest, purchaseResponse, user, quoteStorage.getId(), Optional.of(affiliateLink), null, null, (long) 180);

        assertNotNull((Object) purchase);
    }
}