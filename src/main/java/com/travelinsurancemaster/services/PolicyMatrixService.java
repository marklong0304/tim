package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.*;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.EnumUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ritchie on 4/25/16.
 */
@Service
@Transactional
public class PolicyMatrixService {
    private static final Logger log = LoggerFactory.getLogger(PolicyMatrixService.class);
    private Map<Long, LocalStorage> localStoragesMap = new HashMap<>();

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private CalculatedRestrictionService calculatedRestrictionService;

    /**
     * Local storage is used to store states in multiple browser's tabs.
     * @param sessionId - id of a states for a concrete tab.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public synchronized LocalStorage getLocalStorage(Long sessionId, Long policyMetaId) {
        if (localStoragesMap.get(sessionId) == null) {
            PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
            putInLocalStorageMap(sessionId,
                    getPolicyMetaCategories(policyMetaId),
                    policyMeta.getPolicyMetaRestrictions());
        }

        return localStoragesMap.get(sessionId);
    }

    /**
     * Сервис-таск для очистки локального хранилища
    */
    @Scheduled(fixedRateString = "600000", initialDelay = 0)
    public void cleanUpLocalStorage() {
        if (this.localStoragesMap.size() == 0){
            return;
        }

        log.debug("A cleaning Local Storage started up. Size:  " + this.localStoragesMap.size());
        int cnt = 0;
        for (Iterator<LocalStorage> it = this.localStoragesMap.values().iterator(); it.hasNext(); ) {
            LocalStorage localStorage = it.next();
            if (System.currentTimeMillis() - localStorage.getLastAccess() > 3600000) {
                it.remove();
                cnt++;
            }
        }

        log.debug("Cleaning local storage completed. Removed: " + cnt);
    }

    /**
     * @return policyMetaCategories structure for displaying on policy matrix page.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public MultiValueMap<Long, PolicyMetaCategory> getPolicyMetaCategories(Long policyMetaId) {
        MultiValueMap<Long, PolicyMetaCategory> policyCategoriesWithCode = new LinkedMultiValueMap<>();
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId, false, true, true, false, false, false, false);
        List<PolicyMetaCategory> policyMetaCategories = policyMeta.getPolicyMetaCategories();
        Collections.sort(policyMetaCategories, Collections.reverseOrder());
        for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
            for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategory.getValues()) {
                Hibernate.initialize(policyMetaCategoryValue.getSubcategoryValuesList());
            }
            Category category = policyMetaCategory.getCategory();
            Collections.sort(policyMetaCategory.getValues());
            policyCategoriesWithCode.add(category.getId(), policyMetaCategory);
        }
        return policyCategoriesWithCode;
    }

    /**
     * Save all changes made on policy matrix page
     */
    public void saveAll(MultiValueMap<Long, PolicyMetaCategory> policyMetaCategoriesChangesMap,
                        List<PolicyMetaRestriction> policyMetaRestrictionsChangesList,
                        Long policyMetaId) {
        MultiValueMap<Long, PolicyMetaCategory> policyMetaCategoriesOriginMap = getPolicyMetaCategories(policyMetaId);
        removeOld(policyMetaCategoriesChangesMap, policyMetaCategoriesOriginMap);
        // edit and add new entities
        for (Map.Entry<Long, List<PolicyMetaCategory>> entry : policyMetaCategoriesChangesMap.entrySet()) {
            for (PolicyMetaCategory policyMetaCategory : entry.getValue()) {
                // category is new, not persisted before
                if (policyMetaCategory.getId() == null) {
                    policyMetaCategoryService.createPolicyMetaCategory(policyMetaCategory);
                } else {
                    editCategoryValueAndRestrictions(policyMetaCategoriesOriginMap, policyMetaCategory);
                }
            }
        }

        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(policyMetaId);
        List<PolicyMetaRestriction> policyMetaRestrictionsOriginList = policyMeta.getPolicyMetaRestrictions();
        Set<Long> policyMetaRestrictionsChangedIds = policyMetaRestrictionsChangesList.stream()
                .filter((r) -> r.getId() != null)
                .map((r) -> r.getId())
                .collect(Collectors.toSet());

        Set<Long> policyMetaRestrictionsOriginIds = policyMetaRestrictionsOriginList.stream()
                .map((r) -> r.getId())
                .collect(Collectors.toSet());

        policyMetaRestrictionsChangesList
                .forEach((r) -> r.setPolicyMeta(policyMeta));

        List<PolicyMetaRestriction> mustBeRemove = policyMetaRestrictionsOriginList.stream()
                .filter((r) -> !policyMetaRestrictionsChangedIds.contains(r.getId()))
                .collect(Collectors.toList());

        policyMetaRestrictionsChangesList.stream()
                .filter((r) -> r.getId() == null)
                .forEach((r) -> {
                    restrictionService.save(r);
                    policyMeta.getPolicyMetaRestrictions().add(r);
                });

        policyMetaRestrictionsChangesList.stream()
                .filter((r) -> policyMetaRestrictionsOriginIds.contains(r.getId()))
                .forEach(restrictionService::save);

        mustBeRemove.stream()
                .forEach((r) -> {
                    restrictionService.remove(r);
                    policyMeta.getPolicyMetaRestrictions().remove(r);
                });
    }

    private void editCategoryValueAndRestrictions(MultiValueMap<Long, PolicyMetaCategory> policyMetaCategoriesOriginMap, PolicyMetaCategory policyMetaCategory) {
        outerloop:
        for (Map.Entry<Long, List<PolicyMetaCategory>> entry : policyMetaCategoriesOriginMap.entrySet()) {
            List<PolicyMetaCategory> policyMetaCategoriesOrigin = entry.getValue();
            for (PolicyMetaCategory policyMetaCategoryOrigin : policyMetaCategoriesOrigin) {
                if (policyMetaCategory.getId().equals(policyMetaCategoryOrigin.getId())) {
                    // edit restrictions
                    for (PolicyMetaCategoryRestriction policyMetaCategoryRestriction : policyMetaCategory.getPolicyMetaCategoryRestrictions()) {
                        boolean editedRestriction = true;
                        for (PolicyMetaCategoryRestriction policyMetaCategoryRestrictionOrigin : policyMetaCategoryOrigin.getPolicyMetaCategoryRestrictions()) {
                            if (policyMetaCategoryRestriction.getRestrictionType() == policyMetaCategoryRestrictionOrigin.getRestrictionType()
                                    && policyMetaCategoryRestriction.getRestrictionPermit() == policyMetaCategoryRestrictionOrigin.getRestrictionPermit()
                                    && Objects.equals(policyMetaCategoryRestriction.getMinValue(), policyMetaCategoryRestrictionOrigin.getMinValue())
                                    && Objects.equals(policyMetaCategoryRestriction.getMaxValue(), policyMetaCategoryRestrictionOrigin.getMaxValue())
                                    && Objects.equals(policyMetaCategoryRestriction.getCalculatedRestrictions(), policyMetaCategoryRestrictionOrigin.getCalculatedRestrictions())
                                    && SetUtils.isEqualSet(policyMetaCategoryRestriction.getCountries(), policyMetaCategoryRestrictionOrigin.getCountries())
                                    && SetUtils.isEqualSet(policyMetaCategoryRestriction.getStates(), policyMetaCategoryRestrictionOrigin.getStates())) {
                                editedRestriction = false;
                                break;
                            }
                        }
                        if (editedRestriction) {
                            restrictionService.save(policyMetaCategoryRestriction);
                        }
                    }
                    // edit category values
                    List<PolicyMetaCategoryValue> categoryValuesOrigin = policyMetaCategoryOrigin.getValues();
                    List<PolicyMetaCategoryValue> categoryValues = policyMetaCategory.getValues();
                    if (categoryValuesOrigin.size() != categoryValues.size()) {
                        policyMetaCategoryService.save(policyMetaCategory);
                        break outerloop;
                    } else {
                        for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
                            boolean editedCategoryValue = true;
                            for (PolicyMetaCategoryValue categoryValueOrigin : categoryValuesOrigin) {
                                if (policyMetaCategoryValue.getValue().equals(categoryValueOrigin.getValue())
                                        && policyMetaCategoryValue.getCaption().equals(categoryValueOrigin.getCaption())
                                        && policyMetaCategoryValue.getValueType() == categoryValueOrigin.getValueType()
                                        && Objects.equals(policyMetaCategoryValue.getApiValue(), categoryValueOrigin.getApiValue())
                                        && Objects.equals(policyMetaCategoryValue.getDaysAfterFinalPayment(), categoryValueOrigin.getDaysAfterFinalPayment())
                                        && Objects.equals(policyMetaCategoryValue.getDaysAfterInitialDeposit(), categoryValueOrigin.getDaysAfterInitialDeposit())
                                        && Objects.equals(policyMetaCategoryValue.getMinAge(), categoryValueOrigin.getMinAge())
                                        && Objects.equals(policyMetaCategoryValue.getMaxAge(), categoryValueOrigin.getMaxAge())
                                        && Objects.equals(policyMetaCategoryValue.getSortOrder(), categoryValueOrigin.getSortOrder())
                                        && Objects.equals(policyMetaCategoryValue.isSecondary(), categoryValueOrigin.isSecondary())) {
                                    for (SubcategoryValue subcategoryValue : policyMetaCategoryValue.getSubcategoryValuesList()) {
                                        boolean editedSubcategoryValue = true;
                                        for (SubcategoryValue subcategoryValueOrigin : categoryValueOrigin.getSubcategoryValuesList()) {
                                            if (Objects.equals(subcategoryValue.getSubcategoryValue(), subcategoryValueOrigin.getSubcategoryValue())) {
                                                editedSubcategoryValue = false;
                                                break;
                                            }
                                        }
                                        if (editedSubcategoryValue) {
                                            policyMetaCategoryService.save(policyMetaCategory);
                                            break outerloop;
                                        }
                                    }
                                    editedCategoryValue = false;
                                    break;
                                }
                            }
                            if (editedCategoryValue) {
                                policyMetaCategoryService.save(policyMetaCategory);
                                break outerloop;
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeOld(MultiValueMap<Long, PolicyMetaCategory> policyMetaCategoriesChangesMap, MultiValueMap<Long, PolicyMetaCategory> policyMetaCategoriesOrigin) {
        for (Map.Entry<Long, List<PolicyMetaCategory>> entry : policyMetaCategoriesOrigin.entrySet()) {
            Long categoryId = entry.getKey();
            // no more category existed compare to origin map
            if (policyMetaCategoriesChangesMap.get(categoryId) == null || policyMetaCategoriesChangesMap.get(categoryId).size() == 0) {
                for (PolicyMetaCategory policyMetaCategory : entry.getValue()) {
                    policyMetaCategoryService.deletePolicyMetaCategory(policyMetaCategory);
                }
                continue;
            }
            for (PolicyMetaCategory policyMetaCategoryOrigin : entry.getValue()) {
                List<PolicyMetaCategory> policyMetaCategories = policyMetaCategoriesChangesMap.get(categoryId);
                removePolicyMetaCategory(policyMetaCategories, policyMetaCategoryOrigin);
            }
        }
    }

    private void removePolicyMetaCategory(List<PolicyMetaCategory> policyMetaCategories, PolicyMetaCategory policyMetaCategoryOrigin) {
        boolean foundPolicyMetaCategory = false;
        for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
            if (policyMetaCategory.getId() != null && policyMetaCategory.getId().equals(policyMetaCategoryOrigin.getId())) {
                foundPolicyMetaCategory = true;
                removeRestrictions(policyMetaCategory.getPolicyMetaCategoryRestrictions(), policyMetaCategoryOrigin);
                break;
            }
        }
        if (!foundPolicyMetaCategory) {
            policyMetaCategoryService.deletePolicyMetaCategory(policyMetaCategoryOrigin);
        }
    }

    private void removeRestrictions(List<PolicyMetaCategoryRestriction> policyMetaCategoryRestrictions, PolicyMetaCategory policyMetaCategoryOrigin) {
        boolean foundPolicyMetaRestriction = true;
        outerloop:
        for (Iterator<PolicyMetaCategoryRestriction> iterator = policyMetaCategoryOrigin.getPolicyMetaCategoryRestrictions().iterator(); iterator.hasNext();) {
            PolicyMetaCategoryRestriction policyMetaCategoryRestrictionOrigin = iterator.next();
            if (!policyMetaCategoryRestrictions.contains(policyMetaCategoryRestrictionOrigin)) {
                for (PolicyMetaCategoryRestriction policyMetaCategoryRestriction : policyMetaCategoryRestrictions) {
                    if (policyMetaCategoryRestriction.getId() != null &&
                            policyMetaCategoryRestrictionOrigin.getId() != null &&
                            policyMetaCategoryRestriction.getId().equals(policyMetaCategoryRestrictionOrigin.getId())) {
                        break outerloop;
                    }
                }
                iterator.remove();
                foundPolicyMetaRestriction = false;
            }
        }
        if (!foundPolicyMetaRestriction) {
            policyMetaCategoryService.save(policyMetaCategoryOrigin);
        }
    }

    /**
     * @return filtered by destination, resident or citizen policy meta categories
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public MultiValueMap<Long, PolicyMetaCategory> getPolicyMetaCategoriesFilteredMap(Long policyMetaId, String[] residenceSelectedArray, String[] citizenshipSelectedArray, String[] destinationSelectedArray) {
        MultiValueMap<Long, PolicyMetaCategory> policyMetaCategoriesFilteredMap = getPolicyMetaCategories(policyMetaId);
        for (Map.Entry<Long, List<PolicyMetaCategory>> entry : policyMetaCategoriesFilteredMap.entrySet()) {
            for (Iterator<PolicyMetaCategory> iterator = entry.getValue().iterator(); iterator.hasNext();) {
                PolicyMetaCategory policyMetaCategory = iterator.next();
                outerloop:
                for (PolicyMetaCategoryRestriction policyMetaCategoryRestriction : policyMetaCategory.getPolicyMetaCategoryRestrictions()) {
                    if (policyMetaCategoryRestriction.getRestrictionType() == Restriction.RestrictionType.RESIDENT) {
                        for (String residence : residenceSelectedArray) {
                            if (residence.contains("-")) {
                                String state = residence.split("-")[1];
                                StateCode stateCode = EnumUtils.getEnum(StateCode.class, state);
                                if (!calculatedRestrictionService.isValid(stateCode, policyMetaCategoryRestriction)) {
                                    iterator.remove();
                                    break outerloop;
                                }
                            } else {
                                CountryCode countryCode = EnumUtils.getEnum(CountryCode.class, residence);
                                if (!calculatedRestrictionService.isValid(countryCode, policyMetaCategoryRestriction)) {
                                    iterator.remove();
                                    break outerloop;
                                }
                            }
                        }
                    } else if (policyMetaCategoryRestriction.getRestrictionType() == Restriction.RestrictionType.CITIZEN) {
                        for (String citizenship : citizenshipSelectedArray) {
                            CountryCode countryCode = EnumUtils.getEnum(CountryCode.class, citizenship);
                            if (!calculatedRestrictionService.isValid(countryCode, policyMetaCategoryRestriction)) {
                                iterator.remove();
                                break outerloop;
                            }
                        }
                    } else if (policyMetaCategoryRestriction.getRestrictionType() == Restriction.RestrictionType.DESTINATION) {
                        for (String destination : destinationSelectedArray) {
                            CountryCode countryCode = EnumUtils.getEnum(CountryCode.class, destination);
                            if (!calculatedRestrictionService.isValid(countryCode, policyMetaCategoryRestriction)) {
                                iterator.remove();
                                break outerloop;
                            }
                        }
                    }
                }
            }
        }
        return policyMetaCategoriesFilteredMap;
    }

    public void putInLocalStorageMap(Long sessionId, MultiValueMap<Long, PolicyMetaCategory> policyMetaCategoriesMap, List<PolicyMetaRestriction> policyMetaRestrictions) {
        localStoragesMap.put(sessionId,
                new LocalStorage(policyMetaCategoriesMap,
                        policyMetaRestrictions));
    }

    public class LocalStorage {
        private MultiValueMap<Long, PolicyMetaCategory> categoriesMap = new LinkedMultiValueMap<>();
        private List<PolicyMetaRestriction> restrictionsList = new LinkedList<>();
        private Long lastAccess = new Long(0);

        public LocalStorage(MultiValueMap<Long, PolicyMetaCategory> categoriesMap, List<PolicyMetaRestriction> restrictionsList) {
            this.categoriesMap = categoriesMap;
            this.restrictionsList = restrictionsList;
            this.restrictionsList.sort((r1, r2) -> r1.getId().compareTo(r2.getId()));
            this.lastAccess = System.currentTimeMillis();
        }

        public MultiValueMap<Long, PolicyMetaCategory> getCategoriesMap() {
            setLastAccess();
            return categoriesMap;
        }

        public void setCategoriesMap(MultiValueMap<Long, PolicyMetaCategory> categoriesMap) {
            setLastAccess();
            this.categoriesMap = categoriesMap;
        }

        public List<PolicyMetaRestriction> getRestrictionsList() {
            setLastAccess();
            return restrictionsList;
        }

        public void setRestrictionsList(List<PolicyMetaRestriction> restrictionsList) {
            setLastAccess();
            this.restrictionsList = restrictionsList;
            this.restrictionsList.sort((r1, r2) -> r1.getId().compareTo(r2.getId()));
        }

        public List<PolicyMetaCategory> getCategoriesById(Long categoryId) {
            setLastAccess();
            return categoriesMap.get(categoryId);
        }

        public Long getLastAccess(){
            return this.lastAccess;
        }

        private void setLastAccess() {
            this.lastAccess = System.currentTimeMillis();
        }
    }
}
