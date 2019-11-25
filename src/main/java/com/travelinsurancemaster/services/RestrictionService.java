package com.travelinsurancemaster.services;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.repository.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by ritchie on 4/13/15.
 */


@Service
@Transactional
public class RestrictionService {

    private final PolicyMetaRestrictionRepository policyMetaRestrictionRepository;
    private final PolicyMetaCategoryRestrictionRepository policyMetaCategoryRestrictionRepository;
    private final PolicyMetaCodeRestrictionRepository policyMetaCodeRestrictionRepository;
    private final PolicyMetaPackageRestrictionRepository policyMetaPackageRestrictionRepository;
    private final PolicyQuoteParamRestrictionRepository policyQuoteParamRestrictionRepository;
    private final CalculatedRestrictionService calculatedRestrictionService;

    /**
     * Validates policy meta categories by restrictions
     *
     * @return true if valid, false otherwise
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isValid(QuoteRequest request, List<? extends Restriction> restrictions) {
        if (CollectionUtils.isEmpty(restrictions)) {
            return true;
        }
        for (Restriction restriction : restrictions) {
            if (!calculatedRestrictionService.isValidRestriction(request, restriction)) {
                return false;
            }
        }
        return true;
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyMetaRestriction save(PolicyMetaRestriction restrictionUpd) {
        PolicyMetaRestriction policyMetaRestriction;
        if (restrictionUpd.getId() != null && (policyMetaRestriction = policyMetaRestrictionRepository.findOne(restrictionUpd.getId())) != null) {
            policyMetaRestriction = populate(restrictionUpd, policyMetaRestriction);
            return policyMetaRestrictionRepository.saveAndFlush(policyMetaRestriction);
        }
        return policyMetaRestrictionRepository.saveAndFlush(restrictionUpd);
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyMetaCategoryRestriction save(PolicyMetaCategoryRestriction restrictionUpd) {
        PolicyMetaCategoryRestriction policyMetaCategoryRestriction;
        if (restrictionUpd.getId() != null && (policyMetaCategoryRestriction = policyMetaCategoryRestrictionRepository.findOne(restrictionUpd.getId())) != null) {
            policyMetaCategoryRestriction = populate(restrictionUpd, policyMetaCategoryRestriction);
            return policyMetaCategoryRestrictionRepository.saveAndFlush(policyMetaCategoryRestriction);
        }
        return policyMetaCategoryRestrictionRepository.saveAndFlush(restrictionUpd);
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyMetaCodeRestriction save(PolicyMetaCodeRestriction restrictionUpd) {
        PolicyMetaCodeRestriction policyMetaCodeRestriction;
        if (restrictionUpd.getId() != null && (policyMetaCodeRestriction = policyMetaCodeRestrictionRepository.findOne(restrictionUpd.getId())) != null) {
            policyMetaCodeRestriction = populate(restrictionUpd, policyMetaCodeRestriction);
            return policyMetaCodeRestrictionRepository.saveAndFlush(policyMetaCodeRestriction);
        }
        return policyMetaCodeRestrictionRepository.saveAndFlush(restrictionUpd);
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyMetaPackageRestriction save(PolicyMetaPackageRestriction restrictionUpd) {
        PolicyMetaPackageRestriction policyMetaPackageRestriction;
        if (restrictionUpd.getId() != null && (policyMetaPackageRestriction = policyMetaPackageRestrictionRepository.findOne(restrictionUpd.getId())) != null) {
            policyMetaPackageRestriction = populate(restrictionUpd, policyMetaPackageRestriction);
            return policyMetaPackageRestrictionRepository.saveAndFlush(policyMetaPackageRestriction);
        }
        return policyMetaPackageRestrictionRepository.saveAndFlush(restrictionUpd);
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyQuoteParamRestriction save(PolicyQuoteParamRestriction policyQuoteParamRestrictionUpd) {
        PolicyQuoteParamRestriction policyQuoteParamRestriction;
        if (policyQuoteParamRestrictionUpd.getId() != null && (policyQuoteParamRestriction = policyQuoteParamRestrictionRepository.findOne(policyQuoteParamRestrictionUpd.getId())) != null) {
            policyQuoteParamRestriction = populate(policyQuoteParamRestrictionUpd, policyQuoteParamRestriction);
            return policyQuoteParamRestrictionRepository.saveAndFlush(policyQuoteParamRestriction);
        }
        return policyQuoteParamRestrictionRepository.saveAndFlush(policyQuoteParamRestrictionUpd);
    }

    private <T extends Restriction> T populate(Restriction restrictionUpd, T restriction) {
        restriction.setModified(new Date());
        restriction.setRestrictionType(restrictionUpd.getRestrictionType());
        restriction.setRestrictionPermit(restrictionUpd.getRestrictionPermit());
        restriction.setMinValue(restrictionUpd.getMinValue());
        restriction.setMaxValue(restrictionUpd.getMaxValue());
        restriction.setCountries(restrictionUpd.getCountries());
        restriction.setStates(restrictionUpd.getStates());
        restriction.setCalculatedRestrictions(restrictionUpd.getCalculatedRestrictions());
        return restriction;
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void remove(PolicyMetaRestriction policyMetaRestriction) {
        policyMetaRestrictionRepository.delete(policyMetaRestriction);
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void remove(Long policyMetaCategoryRestrictionId) {
        policyMetaCategoryRestrictionRepository.delete(policyMetaCategoryRestrictionId);
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void remove(PolicyMetaCodeRestriction policyMetaCodeRestriction) {
        policyMetaCodeRestrictionRepository.delete(policyMetaCodeRestriction);
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void remove(PolicyMetaPackageRestriction policyMetaPackageRestriction) {
        policyMetaPackageRestrictionRepository.delete(policyMetaPackageRestriction);
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void remove(PolicyQuoteParamRestriction policyQuoteParamRestriction) {
        policyQuoteParamRestrictionRepository.delete(policyQuoteParamRestriction);
    }

    @Transactional(readOnly = true)
    public PolicyMetaRestriction getPolicyMetaRestrictionById(Long id) {
        return policyMetaRestrictionRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public PolicyMetaCategoryRestriction getPolicyMetaCategoryRestrictionById(Long id) {
        return policyMetaCategoryRestrictionRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public PolicyMetaCodeRestriction getPolicyMetaCodeRestrictionById(Long id) {
        return policyMetaCodeRestrictionRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public PolicyMetaPackageRestriction getPolicyMetaPackageRestrictionById(Long id) {
        return policyMetaPackageRestrictionRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public PolicyQuoteParamRestriction getPolicyQuoteParamRestrictionById(Long id) {
        return policyQuoteParamRestrictionRepository.findOne(id);
    }

    public RestrictionService(PolicyMetaRestrictionRepository policyMetaRestrictionRepository, PolicyMetaCategoryRestrictionRepository policyMetaCategoryRestrictionRepository, PolicyMetaCodeRestrictionRepository policyMetaCodeRestrictionRepository, PolicyMetaPackageRestrictionRepository policyMetaPackageRestrictionRepository, PolicyQuoteParamRestrictionRepository policyQuoteParamRestrictionRepository, CalculatedRestrictionService calculatedRestrictionService) {
        this.policyMetaRestrictionRepository = policyMetaRestrictionRepository;
        this.policyMetaCategoryRestrictionRepository = policyMetaCategoryRestrictionRepository;
        this.policyMetaCodeRestrictionRepository = policyMetaCodeRestrictionRepository;
        this.policyMetaPackageRestrictionRepository = policyMetaPackageRestrictionRepository;
        this.policyQuoteParamRestrictionRepository = policyQuoteParamRestrictionRepository;
        this.calculatedRestrictionService = calculatedRestrictionService;
    }
}
