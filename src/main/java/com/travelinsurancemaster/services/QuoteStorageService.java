package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.dto.QuoteStorage;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.repository.QuoteStorageRepository;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.JsonUtils;
import com.travelinsurancemaster.util.SecurityHelper;
import com.travelinsurancemaster.web.QuoteInfoSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Alexander.Isaenco
 */

@Service
public class QuoteStorageService {

    private final QuoteStorageRepository quoteStorageRepository;
    private final CookieService cookieService;

    public QuoteStorage findOne(String id) {
        return quoteStorageRepository.findOne(id);
    }

    public QuoteStorage getByIdAndDeletedFalse(String id) {
        return quoteStorageRepository.findByIdAndDeletedFalse(id);
    }

    public QuoteStorageService(QuoteStorageRepository quoteStorageRepository, CookieService cookieService) {
        this.quoteStorageRepository = quoteStorageRepository;
        this.cookieService = cookieService;
    }

    public List<QuoteStorage> getSavedQuotesForCurrentUser(User user, LocalDate dateFrom, LocalDate dateTo) {
        if (user == null) {
            return Collections.emptyList();
        }
        if(dateFrom != null && dateTo != null) {
            return quoteStorageRepository.findAllByDeletedFalseAndSavedTrueAndUserAndCreateDateGreaterThanEqualAndCreateDateLessThanEqualOrderByCreateDateDesc(
                    user, DateUtil.fromLocalDate(dateFrom), DateUtil.getEndOfDay(DateUtil.fromLocalDate(dateTo))
            );
        } else if(dateFrom != null) {
            return quoteStorageRepository.findAllByDeletedFalseAndSavedTrueAndUserAndCreateDateGreaterThanEqualOrderByCreateDateDesc(user, DateUtil.fromLocalDate(dateFrom));
        } else if(dateTo != null) {
            return quoteStorageRepository.findAllByDeletedFalseAndSavedTrueAndUserAndCreateDateLessThanEqualOrderByCreateDateDesc(user, DateUtil.getEndOfDay(DateUtil.fromLocalDate(dateTo)));
        } else {
            return quoteStorageRepository.findAllByDeletedFalseAndSavedTrueAndUserOrderByCreateDateDesc(user);
        }
    }

    private QuoteStorage getCurrentQuoteStorage(HttpServletRequest request) {
        QuoteStorage quoteStorage;
        User user = SecurityHelper.getCurrentUser();
        if (user == null) {
            String uid = cookieService.getCookieUid(request);
            quoteStorage = quoteStorageRepository.findTop1ByUidAndDeletedFalseOrderByCreateDateDesc(uid);
        } else {
            quoteStorage = quoteStorageRepository.findTop1ByUserAndDeletedFalseOrderByCreateDateDesc(user);
        }
        return quoteStorage;
    }

    public QuoteRequest getCurrentQuoteRequest(HttpServletRequest request) {
        QuoteStorage quoteStorage = getCurrentQuoteStorage(request);
        return quoteStorage != null ? quoteStorage.getQuoteRequestObj() : null;
    }


    public String getCurrentQuote(HttpServletRequest request) {
        QuoteStorage quoteStorage = getCurrentQuoteStorage(request);
        String id = null;
        try {
            if (quoteStorage != null && quoteStorage.getQuoteRequestObj() != null
                    && quoteStorage.getQuoteRequestObj().getDepartDate() != null
                    && quoteStorage.getQuoteRequestObj().getDepartDate().compareTo(DateUtil.getLocalDateNow(quoteStorage.getQuoteRequestObj().getTimezoneOffset())) > 0) {
                id = quoteStorage.getId();
            }
        } catch(Exception e) {}
        return id;
    }

    @Transactional
    public QuoteStorage saveNewQuote(String oldQuoteId, String quoteRequestJson, User currentUser) {
        QuoteStorage oldQs = findOne(oldQuoteId);
        QuoteStorage newQs = new QuoteStorage(oldQs);
        newQs.setUser(currentUser);
        newQs.setQuoteRequestJson(quoteRequestJson);
        newQs.setCreateDate(new Date());
        newQs = quoteStorageRepository.saveAndFlush(newQs);
        QuoteRequest quoteRequest = JsonUtils.getObject(quoteRequestJson, QuoteRequest.class);
        quoteRequest.setQuoteId(newQs.getId());
        newQs.setQuoteRequestJson(JsonUtils.getJsonString(quoteRequest));
        return quoteStorageRepository.saveAndFlush(newQs);
    }

    @Transactional
    public QuoteStorage save(QuoteStorage quoteStorage){
        return quoteStorageRepository.saveAndFlush(quoteStorage);
    }

    public QuoteStorage saveQuote(QuoteRequest quoteRequest, QuoteStorage quoteStorage, User currentUser, PolicyMeta policyMeta,
                                  HttpServletRequest request, HttpServletResponse response, boolean original,
                                  QuoteInfoSession quoteInfoSession) {
        return saveQuote(quoteRequest, quoteStorage, currentUser, policyMeta, request, response, original, quoteInfoSession, false);
    }

    @Transactional
    public QuoteStorage saveQuote(QuoteRequest quoteRequest, QuoteStorage quoteStorage, User currentUser, PolicyMeta policyMeta,
                                  HttpServletRequest request, HttpServletResponse response, boolean original,
                                  QuoteInfoSession quoteInfoSession, boolean systemSaved) {
        if (quoteStorage.getUser() == null) {
            quoteStorage.setUser(currentUser);
        }
        User affiliate = null;
        if (quoteStorage.getAffiliate() != null && currentUser != null && !Objects.equals(quoteStorage.getAffiliate().getId(), currentUser.getId())) {
            affiliate = quoteStorage.getAffiliate();
        }
        if ((!quoteStorage.isSaved() && currentUser != null && Objects.equals(currentUser.getId(), quoteStorage.getUser().getId())) || systemSaved) {
            quoteRequest.setQuoteId(quoteStorage.getId());
            quoteStorage.setQuoteRequestJson(JsonUtils.getJsonString(quoteRequest));
            quoteStorage.setAffiliate(affiliate);
            quoteStorage.setSaved(true);
            quoteStorage.setOriginal(original);
            quoteStorage.setSystemSaved(systemSaved);
            if (policyMeta != null) {
                quoteStorage.setPolicyUniqueCode(policyMeta.getUniqueCode());
                quoteStorage.setType(QuoteStorage.QuoteType.DETAILS);
            }
            return quoteStorageRepository.saveAndFlush(quoteStorage);
        } else {
            return quoteInfoSession.saveNewQuoteStorage(quoteRequest, request, response, affiliate, policyMeta, true, original, systemSaved);
        }

    }

    @Transactional
    public QuoteStorage deleteQuoteStorage(String quoteId) {
        QuoteStorage quoteStorage = quoteStorageRepository.findOne(quoteId);
        if (quoteStorage == null) {
            return null;
        }
        quoteStorage.setDeleted(true);
        return quoteStorageRepository.save(quoteStorage);
    }

    @Transactional
    public QuoteStorage removeAffiliate(String quoteId) {
        QuoteStorage quoteStorage = quoteStorageRepository.findOne(quoteId);
        if (quoteStorage == null) {
            return null;
        }
        quoteStorage.setAffiliate(null);
        return quoteStorageRepository.save(quoteStorage);
    }

    @Transactional
    public void insertVendorPolicyBasePrice(String quoteId, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode, BigDecimal basePrice) {
        quoteStorageRepository.insertVendorPolicyMap(
                quoteId,
                "{basePrices," + policyMeta.getVendor().getCode() + "}",
                "{basePrices," + policyMeta.getVendor().getCode() + "," + policyMetaCode.getCode() + "}",
                String.valueOf(basePrice),
                "{\"" + policyMetaCode.getCode() + "\":" + basePrice + "}"
        );
    }

    @Transactional
    public void insertVendorPolicyCategoryPrices(String quoteId, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode, Map<String, Map<String, BigDecimal>> policyCategoryPrices) {
        quoteStorageRepository.insertVendorPolicyMap(
                quoteId,
                "{categoryPrices," + policyMeta.getVendor().getCode() + "}",
                "{categoryPrices," + policyMeta.getVendor().getCode() + "," + policyMetaCode.getCode() + "}",
                JsonUtils.getJsonString(policyCategoryPrices),
                "{\"" + policyMetaCode.getCode() + "\":" + JsonUtils.getJsonString(policyCategoryPrices) + "}"
        );
    }
}