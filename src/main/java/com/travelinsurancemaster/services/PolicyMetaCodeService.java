package com.travelinsurancemaster.services;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.repository.PolicyMetaCodeRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by ritchie on 4/15/16.
 */

@Service
@Transactional
public class PolicyMetaCodeService {

    @Autowired
    private PolicyMetaCodeRepository policyMetaCodeRepository;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyMetaCode save(PolicyMetaCode policyMetaCodeUpd) {
        return save(policyMetaCodeUpd, null);
    }

    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyMetaCode save(PolicyMetaCode policyMetaCodeUpd, PolicyMeta savedPolicyMeta) {
        PolicyMetaCode policyMetaCode;
        if (policyMetaCodeUpd.getId() != null && (policyMetaCode = policyMetaCodeRepository.findOne(policyMetaCodeUpd.getId())) != null) {
            policyMetaCode.setPolicyMeta(policyMetaCodeUpd.getPolicyMeta());
            policyMetaCode.setCode(policyMetaCodeUpd.getCode());
            policyMetaCode.setName(policyMetaCodeUpd.getName());
            return policyMetaCodeRepository.saveAndFlush(policyMetaCode);
        }
        if (savedPolicyMeta != null) {
            policyMetaCodeUpd.setPolicyMeta(savedPolicyMeta);
        }
        return policyMetaCodeRepository.saveAndFlush(policyMetaCodeUpd);
    }

    /**
     * @return policy meta code by restrictions.
     * One policy can have multiple policy meta codes depending on restrictions.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public PolicyMetaCode getPolicyMetaCode(Long policyMetaId, QuoteRequest quoteRequest) {
        if (policyMetaId == null) {
            return null;
        }
        PolicyMeta policyMeta = policyMetaService.getCached(policyMetaId);
        if (policyMeta == null) {
            return null;
        }
        List<PolicyMetaCode> policyMetaCodesList = new ArrayList<>(policyMeta.getPolicyMetaCodes());
        if (quoteRequest == null) {
            for (PolicyMetaCode policyMetaCode : policyMetaCodesList) {
                if (CollectionUtils.isEmpty(policyMetaCode.getPolicyMetaCodeRestrictions())) {
                    return policyMetaCode;
                }
            }
            return policyMetaCodesList.get(0);
        }
        Collections.sort(policyMetaCodesList);
        Optional<PolicyMetaCode> policyMetaCodeByResrictions = policyMetaCodesList.stream()
                .filter(policyMetaCode -> restrictionService.isValid(quoteRequest, policyMetaCode.getPolicyMetaCodeRestrictions()))
                .findFirst();
        if (!policyMetaCodeByResrictions.isPresent()) {
            return null;
        }
        return policyMetaCodeByResrictions.get();
    }

    @Transactional(readOnly = true)
    public PolicyMetaCode getPolicyMetaCodeById(Long id) {
        return policyMetaCodeRepository.findOne(id);
    }

    @CacheEvict(value = {CacheConfig.PRODUCTS_CACHE, CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void remove(PolicyMetaCode policyMetaCode) {
        policyMetaCodeRepository.delete(policyMetaCode);
    }

    @Transactional(readOnly = true)
    public PolicyMetaCode getPolicyMetaCodeFirst(Long policyMetaId) {
        return policyMetaCodeRepository.findFirstByPolicyMetaId(policyMetaId);
    }
}
