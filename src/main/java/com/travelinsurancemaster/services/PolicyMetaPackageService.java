package com.travelinsurancemaster.services;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.PolicyMetaPackage;
import com.travelinsurancemaster.model.dto.PolicyMetaPackageValue;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaCategoryContent;
import com.travelinsurancemaster.repository.PolicyMetaPackageRepository;
import com.travelinsurancemaster.services.cms.PolicyMetaCategoryContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by Chernov Artur on 04.06.15.
 */

@Service
@Transactional
public class PolicyMetaPackageService {

    @Autowired
    private PolicyMetaPackageRepository policyMetaPackageRepository;

    @Autowired
    private PolicyMetaCategoryContentService policyMetaCategoryContentService;

    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyMetaPackage save(PolicyMetaPackage policyMetaPackageUpd) {
        return policyMetaPackageRepository.saveAndFlush(policyMetaPackageUpd);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PolicyMetaPackage findByPolicyMetaPackageValue(PolicyMeta policyMeta, PolicyMetaCategory policyMetaCategory, String value) {
        List<PolicyMetaPackage> policyMetaPackages = this.policyMetaPackageRepository.findByPolicyMeta(policyMeta);
        for (PolicyMetaPackage policyMetaPackage : policyMetaPackages) {
            for (PolicyMetaPackageValue policyMetaPackageValue : policyMetaPackage.getPolicyMetaPackageValues()) {
                if (policyMetaPackageValue.getPolicyMetaCategory().getCategory().getCode().equals(policyMetaCategory.getCategory().getCode())) {
                    String val = policyMetaPackageValue.getValue();
                    if (val != null && val.equalsIgnoreCase(value)) {
                        return policyMetaPackage;
                    }
                }
            }
        }
        return null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PolicyMetaPackage findByCode(PolicyMeta policyMeta, String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        List<PolicyMetaPackage> policyMetaPackages = this.policyMetaPackageRepository.findByPolicyMeta(policyMeta);
        for (PolicyMetaPackage policyMetaPackage : policyMetaPackages) {
            if (code.equals(policyMetaPackage.getCode())) {
                return policyMetaPackage;
            }
        }
        return null;
    }

    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void deletePolicyMetaPackage(PolicyMetaPackage policyMetaPackage) {
        PolicyMetaCategoryContent policyMetaCategoryContentForPackage = policyMetaCategoryContentService.getPolicyMetaPackageById(policyMetaPackage.getId());
        policyMetaCategoryContentService.deletePolicyMetaCategoryContent(policyMetaCategoryContentForPackage.getId());
        policyMetaPackageRepository.delete(policyMetaPackage);
    }

    @Transactional(readOnly = true)
    public void generatePolicyMetaPackageCode(PolicyMetaPackage policyMetaPackage) {
        if (policyMetaPackage != null && policyMetaPackage.getCode() == null) {
            policyMetaPackage.setCode(UUID.randomUUID().toString());
        }
    }
}
