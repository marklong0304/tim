package com.travelinsurancemaster.services;

import com.google.common.collect.Iterables;
import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaCategoryContent;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.repository.PolicyMetaCategoryRepository;
import com.travelinsurancemaster.repository.PolicyMetaCategoryValueRepository;
import com.travelinsurancemaster.services.cms.PolicyMetaCategoryContentService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 04.06.15.
 */


@Service
@Transactional
public class PolicyMetaCategoryService {

    private static final Logger log = LoggerFactory.getLogger(PolicyMetaCategoryService.class);

    @Autowired
    private PolicyMetaCategoryRepository policyMetaCategoryRepository;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private PolicyMetaCategoryContentService policyMetaCategoryContentService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PolicyMetaPackageService policyMetaPackageService;

    @Autowired
    private SubcategoryValueService subcategoryValueService;

    @Autowired
    private PolicyMetaCategoryValueRepository policyMetaCategoryValueRepository;

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;

    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyMetaCategory save(PolicyMetaCategory policyMetaCategoryUpd) {
        PolicyMetaCategory policyMetaCategory;
        if (policyMetaCategoryUpd.getId() != null && (policyMetaCategory = policyMetaCategoryRepository.findOne(policyMetaCategoryUpd.getId())) != null) {
            policyMetaCategory.setPolicyMeta(policyMetaCategoryUpd.getPolicyMeta());
            policyMetaCategory.setType(policyMetaCategoryUpd.getType());
            policyMetaCategory.setCategory(policyMetaCategoryUpd.getCategory());
            policyMetaCategory.setDescription(policyMetaCategoryUpd.getDescription());

            List<PolicyMetaCategoryValue> savedPolicyMetaCategoryValueList = new ArrayList<>();
            removeManualPolicyMetaCategoryValue(policyMetaCategory, policyMetaCategoryUpd);
            for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategoryUpd.getValues()) {
                if (policyMetaCategoryValue.getSortOrder() == null){
                    policyMetaCategoryValue.setSortOrder(policyMetaCategoryUpd.getValues().size());
                }
                PolicyMetaCategoryValue policyMetaCategoryValueSaved = policyMetaCategoryValueRepository.saveAndFlush(policyMetaCategoryValue);
                for (SubcategoryValue subcategoryValue : policyMetaCategoryValue.getSubcategoryValuesList()) {
                    subcategoryValue.setPolicyMetaCategoryValue(policyMetaCategoryValueSaved);
                    subcategoryValueService.save(subcategoryValue);
                }
                savedPolicyMetaCategoryValueList.add(policyMetaCategoryValueSaved);
            }

            policyMetaCategory.getValues().clear();
            policyMetaCategory.getValues().addAll(savedPolicyMetaCategoryValueList);
            return policyMetaCategoryRepository.saveAndFlush(policyMetaCategory);
        }
        PolicyMetaCategory savedPolicyMetaCategory = policyMetaCategoryRepository.saveAndFlush(policyMetaCategoryUpd);
        for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategoryUpd.getValues()) {
            policyMetaCategoryValue.setPolicyMetaCategory(savedPolicyMetaCategory);
            PolicyMetaCategoryValue policyMetaCategoryValueSaved = policyMetaCategoryValueRepository.saveAndFlush(policyMetaCategoryValue);
            for (SubcategoryValue subcategoryValue : policyMetaCategoryValue.getSubcategoryValuesList()) {
                subcategoryValue.setPolicyMetaCategoryValue(policyMetaCategoryValueSaved);
                subcategoryValueService.save(subcategoryValue);
            }
        }
        return savedPolicyMetaCategory;
    }

    private void removeManualPolicyMetaCategoryValue(PolicyMetaCategory policyMetaCategory, PolicyMetaCategory policyMetaCategoryUpd) {
        for (PolicyMetaCategoryValue policyMetaCategoryValueOld : policyMetaCategory.getValues()) {
            boolean foundOldValue = false;
            for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategoryUpd.getValues()) {
                if (Objects.equals(policyMetaCategoryValue.getId(), policyMetaCategoryValueOld.getId())) {
                    foundOldValue = true;
                    break;
                }
            }
            if (!foundOldValue) {
                policyMetaCategoryValueRepository.delete(policyMetaCategoryValueOld);
            }
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public boolean isPolicyMetaContainsCategory(Long policyMetaId, String categoryCode, QuoteRequest quoteRequest) {
        return getPolicyMetaCategory(policyMetaId, categoryCode, quoteRequest) != null;
    }

    /**
     * @return policy meta category values of policy meta category filtered by restrictions
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<PolicyMetaCategoryValue> getCategoryValues(Long policyMetaId, String categoryCode, QuoteRequest quoteRequest) {
        PolicyMetaCategory policyMetaCategory = getPolicyMetaCategory(policyMetaId, categoryCode, quoteRequest);
        if (policyMetaCategory == null) {
            return Collections.emptyList();
        }
        return policyMetaCategory.getValues();
    }

    /**
     * @return policy meta category values of policy meta category filtered by category value restrictions
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<PolicyMetaCategoryValue> getCategoryValuesFilteredByValueRestrictions(PolicyMetaCategory policyMetaCategory, QuoteRequest quoteRequest) {
        if (policyMetaCategory == null) {
            return Collections.emptyList();
        }
        List<PolicyMetaCategoryValue> categoryValues = policyMetaCategoryValueService.getAllAcceptedCategoryValue(policyMetaCategory.getValues(), quoteRequest);
        if (PolicyMetaCategory.MetaParamType.SIMPLE.equals(policyMetaCategory.getType()) && categoryValues.size() > 1) {
            return Collections.singletonList(Iterables.getLast(categoryValues));

        }
        return categoryValues;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<PolicyMetaCategory> getPolicyMetaCategories(Long policyMetaId, QuoteRequest quoteRequest) {
        if (policyMetaId == null) {
            return Collections.emptyList();
        }
        PolicyMeta cached = policyMetaService.getCached(policyMetaId);
        return getPolicyMetaCategories(cached, quoteRequest);
    }

    /**
     * @return policy meta categories filtered by restrictions for policy meta
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<PolicyMetaCategory> getPolicyMetaCategories(PolicyMeta policyMeta, QuoteRequest quoteRequest) {
        if (policyMeta == null) {
            return Collections.emptyList();
        }
        List<PolicyMetaCategory> policyMetaCategories = new ArrayList<>(policyMeta.getPolicyMetaCategories());
        Collections.sort(policyMetaCategories);
        List<PolicyMetaCategory> policyMetaCategoriesByRestrictions = policyMetaCategories.stream()
                .filter(policyMetaCategory -> restrictionService.isValid(quoteRequest, policyMetaCategory.getPolicyMetaCategoryRestrictions()))
                .collect(Collectors.toList());
        return policyMetaCategoriesByRestrictions;
    }

    /**
     * @return policy meta categories from categories list param filtered by restrictions for policy meta
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<PolicyMetaCategory> getPolicyMetaCategories(Long policyMetaId, QuoteRequest quoteRequest, List<Category> categories) {
        if (policyMetaId == null) {
            return Collections.emptyList();
        }
        PolicyMeta cached = policyMetaService.getCached(policyMetaId);
        if (cached == null) {
            return Collections.emptyList();
        }
        List<PolicyMetaCategory> policyMetaCategories = new ArrayList<>(cached.getPolicyMetaCategories());
        Collections.sort(policyMetaCategories);
        List<PolicyMetaCategory> policyMetaCategoriesByRestrictions = policyMetaCategories.stream()
                .filter(policyMetaCategory -> categories.contains(policyMetaCategory.getCategory()))
                .filter(policyMetaCategory -> restrictionService.isValid(quoteRequest, policyMetaCategory.getPolicyMetaCategoryRestrictions()))
                .collect(Collectors.toList());
        return policyMetaCategoriesByRestrictions;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PolicyMetaCategory getPolicyMetaCategory(PolicyMeta policyMeta, String categoryCode, QuoteRequest quoteRequest) {
        List<PolicyMetaCategory> policyMetaCategories = policyMeta.getPolicyMetaCategories();
        return policyMetaCategories.stream()
                .filter(policyMetaCategory -> StringUtils.equals(categoryCode, policyMetaCategory.getCategory().getCode()))
                .filter(policyMetaCategory -> restrictionService.isValid(quoteRequest, policyMetaCategory.getPolicyMetaCategoryRestrictions()))
                .findAny().orElse(null);
    }

    public PolicyMetaCategory getPolicyMetaCategory(Long policyMetaId, String categoryCode, QuoteRequest quoteRequest) {
        return getPolicyMetaCategory(policyMetaService.getCached(policyMetaId), categoryCode, quoteRequest);
    }

    /**
     * Delete policy meta category and policy meta category content (One-to-One CMS-API)
     */
    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void deletePolicyMetaCategory(PolicyMetaCategory policyMetaCategory) {
        Category category = policyMetaCategory.getCategory();
        PolicyMeta policyMeta = policyMetaCategory.getPolicyMeta();
        PolicyMetaPage policyMetaPage = policyMeta.getPolicyMetaPage();

        PolicyMetaCategoryContent policyMetaCategoryContent = null;

        int categoryValuesCount = 0;
        for (Iterator<PolicyMetaCategory> it = policyMeta.getPolicyMetaCategories().iterator(); it.hasNext(); ) {
            PolicyMetaCategory pmc = it.next();
            if (pmc.getCategory().equals(category))
                categoryValuesCount++;
            if (pmc.getId().equals(policyMetaCategory.getId()))
                it.remove();
        }

        if (policyMetaPage != null && categoryValuesCount < 2) {
            for (Iterator<PolicyMetaCategoryContent> it = policyMetaPage.getPolicyMetaCategoryList().iterator(); it.hasNext(); ) {
                PolicyMetaCategoryContent pmcc = it.next();
                if (pmcc.getCategory().equals(category)) //.getId().equals(category.getId()))
                {
                    policyMetaCategoryContent = pmcc;
                    it.remove();
                    break;
                }
            }
        }
        for (PolicyMetaPackage policyMetaPackage : policyMeta.getPolicyMetaPackages()) {
            for (Iterator<PolicyMetaPackageValue> iterator = policyMetaPackage.getPolicyMetaPackageValues().iterator(); iterator.hasNext(); ) {
                PolicyMetaPackageValue policyMetaPackageValue = iterator.next();
                if (policyMetaPackageValue.getPolicyMetaCategory().getId().equals(policyMetaCategory.getId())) {
                    iterator.remove();
                    policyMetaPackageService.save(policyMetaPackage);
                    break;
                }
            }
        }
        policyMetaCategoryRepository.delete(policyMetaCategory.getId());
        if (policyMetaCategoryContent != null) {
            policyMetaCategoryContentService.deletePolicyMetaCategoryContent(policyMetaCategoryContent.getId());
        }
    }

    /**
     * Creates policy meta category and policy meta category content (One-to-One CMS-API)
     */
    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyMetaCategory createPolicyMetaCategory(PolicyMetaCategory policyMetaCategory) {
        log.debug("Create new policy meta category = {}", policyMetaCategory);
        policyMetaCategory = save(policyMetaCategory);
        // create policy meta category content if not exists
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaCategory.getPolicyMeta().getId());
        policyMetaCategoryContentService.createNewPolicyMetaCategoryContentIfNotExists(policyMeta, policyMetaCategory);
        return policyMetaCategory;
    }

    public void formatValues(PolicyMetaCategory policyMetaCategory) {
        policyMetaCategory.getValues().stream()
                .filter(policyMetaCategoryValue -> policyMetaCategoryValue.getValueType() != ValueType.NAN)
                .forEach(policyMetaCategoryValue -> policyMetaCategoryValue.setValue(policyMetaCategoryValue.getValue().replaceAll("[^0-9]", "")));
    }
}
