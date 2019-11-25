package com.travelinsurancemaster.services;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.model.dto.cms.page.VendorPage;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.PolicyMetaQuoteRequestPair;
import com.travelinsurancemaster.model.webservice.common.Product;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.repository.PolicyMetaCodeRepository;
import com.travelinsurancemaster.repository.PolicyMetaRepository;
import com.travelinsurancemaster.services.cms.VendorPageService;
import com.travelinsurancemaster.util.*;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by ritchie on 4/13/15.
 */

@Service
@Transactional
public class PolicyMetaService {

    private static final Logger log = LoggerFactory.getLogger(PolicyMetaService.class);

    private final boolean D = false;

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    @Autowired
    private PolicyMetaRepository policyMetaRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryCacheService categoryCacheService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private VendorPageService vendorPageService;

    @Autowired
    private Map<String, InsuranceVendorApi> clients;

    @Lazy
    @Autowired
    private PolicyMetaCacheService policyMetaCacheService;

    @Autowired
    private PolicyMetaCodeService policyMetaCodeService;

    @Autowired
    private PolicyMetaCodeRepository policyMetaCodeRepository;

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;

    @Autowired
    private SubcategoryService subcategoryService;

    @Autowired
    private RestrictionService restrictionService;

    @Transactional(readOnly = true)
    public List<PolicyMeta> getAll() {
        return policyMetaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PolicyMeta> getAllActivePolicyMeta() {
        return policyMetaRepository.findByVendorActiveTrueAndActiveTrueAndDeletedDateIsNull();
    }

    @Transactional(readOnly = true)
    public List<PolicyMeta> getAllSorted(boolean initCategories, boolean initRestrictions) {
        List<PolicyMeta> policyMetas = policyMetaRepository.findAll(new Sort(
                new Sort.Order(Sort.Direction.ASC, "vendor.name"),
                new Sort.Order(Sort.Direction.ASC, "displayName")
        ));
        policyMetas.forEach(policyMeta -> {
            PolicyMeta cached = getCached(policyMeta.getId());
            if (initRestrictions) {
                //Hibernate.initialize(policyMeta.getPolicyMetaRestrictions());
                policyMeta.setPolicyMetaRestrictions(cached.getPolicyMetaRestrictions());
            }
            if (initCategories) {
                //Hibernate.initialize(policyMeta.getPolicyMetaCategories());
                //policyMeta.getPolicyMetaCategories().forEach(policyMetaCategory -> Hibernate.initialize(policyMetaCategory.getValues()));
                policyMeta.setPolicyMetaCategories(cached.getPolicyMetaCategories());
            }
        });
        return policyMetas;
    }

    @Transactional(readOnly = true)
    public List<PolicyMetaCode> getPoliciesByCode(String code) {
        return policyMetaCodeRepository.findByCode(code);
    }

    /**
     * @return initialized policy meta.
     */
    @Transactional(readOnly = true)
    public PolicyMeta getPolicyMetaById(Long id) {
        return getPolicyMetaById(id, true, true, true, true, true, true, true);
    }

    @Transactional(readOnly = true)
    public PolicyMeta getPolicyMetaById(Long id, boolean withQuoteParams, boolean withRestrictions,
                                        boolean withCategories, boolean withPercent, boolean withPolicyMetaPage,
                                        boolean withPolicyMetaCodes, boolean withPackages) {
        if (id == null) {
            return null;
        }
        PolicyMeta policyMeta = policyMetaRepository.findOne(id);
        if (policyMeta == null) {
            return null;
        }
        if (withPackages) {
            Hibernate.initialize(policyMeta.getPolicyMetaPackages());
            policyMeta.getPolicyMetaPackages().forEach(packages ->
                    Hibernate.initialize(packages.getPolicyMetaPackageRestrictions())
            );
        }
        if (withQuoteParams) {
            Hibernate.initialize(policyMeta.getPolicyQuoteParams());
            policyMeta.getPolicyQuoteParams().forEach(policyQuoteParam ->
                    Hibernate.initialize(policyQuoteParam.getPolicyQuoteParamRestrictions())
            );
        }
        if (withRestrictions) {
            Hibernate.initialize(policyMeta.getPolicyMetaRestrictions());
        }
        if (withCategories) {
            Hibernate.initialize(policyMeta.getPolicyMetaCategories());
            policyMeta.getPolicyMetaCategories().forEach(policyMetaCategory -> {
                Hibernate.initialize(policyMetaCategory.getCategory());
                Hibernate.initialize(policyMetaCategory.getValues());
                Hibernate.initialize(policyMetaCategory.getPolicyMetaCategoryRestrictions());
                for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategory.getValues()) {
                    Hibernate.initialize(policyMetaCategoryValue.getSubcategoryValuesList());
                }
                if (withPackages) {
                    Hibernate.initialize(policyMetaCategory.getPolicyMetaPackageValues());
                }
            });
        }
        if (withPercent) {
            Hibernate.initialize(policyMeta.getPercentInfo());
        }
        if (withPolicyMetaPage) {
            Hibernate.initialize(policyMeta.getPolicyMetaPage());
            PolicyMetaPage policyMetaPage = policyMeta.getPolicyMetaPage();
            if(policyMetaPage != null) {
                Hibernate.initialize(policyMetaPage.getVendorPage());
                policyMetaPage.getContent();
                policyMetaPage.getPolicyMetaCategoryList().forEach(policyMetaCategoryContent -> {
                    policyMetaCategoryContent.getContent();
                    policyMetaCategoryContent.getCertificateText();
                });
                policyMetaPage.getPolicyMetaCustomCategoryList().forEach(policyMetaCategoryContent -> {
                    policyMetaCategoryContent.getContent();
                    policyMetaCategoryContent.getCertificateText();
                });
                policyMetaPage.getPolicyMetaPlanInfoList().forEach(policyMetaCategoryContent -> {
                    policyMetaCategoryContent.getContent();
                    policyMetaCategoryContent.getCertificateText();
                });
                policyMetaPage.getPolicyMetaRestrictionsList().forEach(policyMetaCategoryContent -> {
                    policyMetaCategoryContent.getContent();
                    policyMetaCategoryContent.getCertificateText();
                });
                policyMetaPage.getPolicyMetaPackageList().forEach(policyMetaCategoryContent -> {
                    policyMetaCategoryContent.getContent();
                    policyMetaCategoryContent.getCertificateText();
                });
            }
        }
        if (withPolicyMetaCodes) {
            Hibernate.initialize(policyMeta.getPolicyMetaCodes());
            policyMeta.getPolicyMetaCodes().forEach(policyMetaCode -> Hibernate.initialize(policyMetaCode.getPolicyMetaCodeRestrictions()));
        }
        return policyMeta;
    }

    @Transactional(readOnly = true)
    public List<PolicyMeta> getAvailableForVendorPage(VendorPage vendorPage) {
        if (vendorPage == null) {
            return Collections.emptyList();
        }
        VendorPage vendorPageById = vendorPageService.getById(vendorPage.getId());
        if (vendorPageById == null) {
            return Collections.emptyList();
        }
        return getByVendor(vendorPageById.getVendor());
    }

    @Transactional(readOnly = true)
    public List<PolicyMeta> getByVendor(Vendor vendor) {
        if (vendor == null) {
            return Collections.emptyList();
        }
        return policyMetaRepository.findByVendorAndDeletedDateIsNull(vendor, ServiceUtils.sortByFieldAscIgnoreCase("displayName"));
    }

    @Transactional(readOnly = true)
    public List<PolicyMetaCode> getPolicyMetas(String vendorCode, String policyMetaCode) {
        return policyMetaCodeRepository.findByCodeAndPolicyMetaVendorCode(policyMetaCode, vendorCode);
    }

    @Transactional(readOnly = true)
    public PolicyMeta getPolicyMetaByUniqueCode(String uniqueCode) {
        return policyMetaRepository.findByUniqueCodeAndDeletedDateIsNull(uniqueCode);
    }

    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyMeta save(PolicyMeta policyMetaUpd) {
        policyMetaUpd.setModified(new Date());
        PolicyMeta policyMeta;
        Vendor vendor = vendorService.getVendorWithLogo(policyMetaUpd.getVendor().getId());
        PolicyMeta savedPolicyMeta;
        if (policyMetaUpd.getId() != null && (policyMeta = policyMetaRepository.findOne(policyMetaUpd.getId())) != null) {
            policyMeta.setModified(policyMetaUpd.getModified());
            policyMeta.setDisplayName(policyMetaUpd.getDisplayName());
            policyMeta.setUniqueCode(policyMetaUpd.getUniqueCode());
            policyMeta.setActive(policyMetaUpd.isActive());
            policyMeta.setPurchasable(policyMetaUpd.isPurchasable());
            policyMeta.setSupportsZeroCancellation(policyMetaUpd.isSupportsZeroCancellation());
            policyMeta.setShowOnQuotes(policyMetaUpd.isShowOnQuotes());
            // policyMeta.setRequiredDepositDate(policyMetaUpd.isRequiredDepositDate());
            policyMeta.setVendor(vendor);
            policyMeta.setPercentType(policyMetaUpd.getPercentType());
            policyMeta.setPercentInfo(policyMetaUpd.getPercentInfo());
            policyMeta.setMinimalTripCost(policyMetaUpd.getMinimalTripCost());
            policyMeta.setPolicyMetaPage(policyMetaUpd.getPolicyMetaPage());
            return policyMetaRepository.saveAndFlush(policyMeta);
        }
        savedPolicyMeta = policyMetaRepository.saveAndFlush(policyMetaUpd);
        policyMetaCodeService.save(policyMetaUpd.getPolicyMetaCodes().get(0), savedPolicyMeta);
        return savedPolicyMeta;
    }

    /**
     * Получение всех PolicyMeta вендора, подходящих под список категорий.
     *
     * @param quoteRequest
     * @param vendorCode
     * @return
     */
    private List<PolicyMeta> getPolicyMetas(QuoteRequest quoteRequest, String vendorCode) {
        if(D) log.debug("Get PolicyMetas by Vendor({}) for QuoteRequest", vendorCode);
        if (StringUtils.isBlank(vendorCode)) {
            return Collections.emptyList();
        }
        List<PolicyMeta> policyMetas = getPolicyMetasForVendor(vendorCode);
        if (CollectionUtils.isEmpty(policyMetas)) {
            if(D) log.warn("{}: NO POLICY META", vendorCode);
            return Collections.emptyList();
        }
        Iterator<PolicyMeta> iter = policyMetas.iterator();
        while (iter.hasNext()) {
            PolicyMeta policyMeta = iter.next();
            if (!isPolicyContainsCategory(policyMeta, QuoteRequest.getClearCategoriesQuoteRequest(quoteRequest))) {
                if(D) log.debug("{} - {}: Removed due to policy restrictions", vendorCode, policyMeta.getUniqueCode());
                iter.remove();
            }
        }
        return policyMetas;
    }

    /**
     * Checks if current policy contains upsales
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isPolicyContainsUpsales(PolicyMeta policyMeta, QuoteRequest quoteRequest) {
        return isPolicyContainsCategory(policyMeta, quoteRequest,  true);
    }

    /**
     * Checks if current policy contains category
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isPolicyContainsCategory(PolicyMeta policyMeta, QuoteRequest quoteRequest) {
        return isPolicyContainsCategory(policyMeta, quoteRequest, false);
    }

    /**
     * Проверка policyMeta по списку категорий.
     *
     * @param policyMeta
     * @param quoteRequest
     * @return
     */
    private boolean isPolicyContainsCategory(PolicyMeta policyMeta, QuoteRequest quoteRequest, boolean checkUpsale) {
        Map<String, String> categories = checkUpsale ? quoteRequest.getUpsaleValues() : quoteRequest.getCategories();
        if (policyMeta == null || !policyMeta.isActive()) {
            return false;
        }
        if (MapUtils.isEmpty(categories)) {
            return true;
        }

        List<PolicyMetaCategory> policyMetaCategories = policyMetaCategoryService.getPolicyMetaCategories(policyMeta, quoteRequest);
        Map<String, Category> categoryMap = categoryCacheService.getCategoryMap();

        for (Map.Entry<String, String> categoryEntry : categories.entrySet()) {
            String categoryCode = categoryEntry.getKey();
            if (!categoryMap.containsKey(categoryCode)) {
                continue;
            }
            Category category = categoryMap.get(categoryCode);
            Integer val = NumberUtils.toInt(categoryEntry.getValue(), CategoryCodes.NOT_SELECTED);
            //exclude all not-selected values
            if (category.getType() == Category.CategoryType.CATALOG &&
                    (val == CategoryCodes.NOT_SELECTED || PolicyMetaCategoryValue.isEmptyValue(categoryEntry.getValue()))) {
                continue;
            }
            boolean found = false;

            if (category.getType() == Category.CategoryType.CONDITIONAL) {
                Binding binding = new Binding();
                GroovyShell groovyShell = new GroovyShell(binding);
                binding.setVariable("util", new ConditionalCategoryUtil(quoteRequest, policyMetaCategories, this));
                try {
                    found = (Boolean) groovyShell.evaluate(category.getCategoryCondition());
                } catch (Throwable e) {
                    found = false;
                }
            } else {
                outer:
                for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
                    if (Objects.equals(category.getId(), policyMetaCategory.getCategory().getId())) {
                        switch (category.getType()) {
                            case SIMPLE:
                                found = checkPolicyMetaConditions(policyMetaCategory, quoteRequest, true);
                                break outer;
                            case CATALOG:
                                for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategory.getValues()) {
                                    Integer categoryVal = policyMetaCategoryValue.getValueByType(category.getValueType(), quoteRequest.getTripCostPerTraveler());
                                    if (category.getCode().equals(CategoryCodes.LOOK_BACK_PERIOD)) {
                                        if (categoryVal != null && val >= categoryVal) {
                                            found = checkPolicyMetaCategoryValueByConditions(policyMetaCategoryValue, quoteRequest, true);
                                            break outer;
                                        }
                                    } else {
                                        if (categoryVal != null && val <= categoryVal) {
                                            found = checkPolicyMetaCategoryValueByConditions(policyMetaCategoryValue, quoteRequest, true);
                                            break outer;
                                        }
                                    }
                                }
                        }
                    }
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks policyMeta conditions
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean checkPolicyMetaConditions(PolicyMetaCategory policyMetaCategory, QuoteRequest quoteRequest, boolean checkPolicyMetaCategoryRestrictionType) {
        if (CollectionUtils.isEmpty(policyMetaCategory.getValues())) {
            return true;
        }
        for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategory.getValues()) {
            if (PolicyMetaCategoryValue.isEmptyValue(policyMetaCategoryValue.getValue())) {
                continue;
            }
            if (checkPolicyMetaCategoryValueByConditions(policyMetaCategoryValue, quoteRequest, checkPolicyMetaCategoryRestrictionType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks policyMeta category value conditions
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean checkPolicyMetaCategoryValueByConditions(PolicyMetaCategoryValue policyMetaCategoryValue, QuoteRequest quoteRequest, boolean checkPolicyMetaCategoryRestrictionType) {
        if (checkPolicyMetaCategoryRestrictionType) {
            //обязательно заполнены DepositDate и PaymentDate, если одна из daysAfterInitialDeposit или daysAfterFinalPayment заполнены
            if ((policyMetaCategoryValue.getDaysAfterInitialDeposit() != null && quoteRequest.getDepositDate() == null) ||
                    (policyMetaCategoryValue.getDaysAfterFinalPayment() != null && quoteRequest.getPaymentDate() == null)) {
                return false;
            }
        }
        if (policyMetaCategoryValue.getDaysAfterInitialDeposit() != null && quoteRequest.getDepositDate() != null) {
            int daysBetweenDepositAndCurrent = (int) ChronoUnit.DAYS.between(quoteRequest.getDepositDate(), DateUtil.getLocalDateNow(quoteRequest.getTimezoneOffset()));
            if (policyMetaCategoryValue.getDaysAfterInitialDeposit() >= 0) {
                if (daysBetweenDepositAndCurrent > policyMetaCategoryValue.getDaysAfterInitialDeposit() || daysBetweenDepositAndCurrent < 0) {
                    return false;
                }
            } else {
                if (daysBetweenDepositAndCurrent < policyMetaCategoryValue.getDaysAfterInitialDeposit() || daysBetweenDepositAndCurrent > 0) {
                    return false;
                }
            }
        }
        if (policyMetaCategoryValue.getDaysAfterFinalPayment() != null && quoteRequest.getPaymentDate() != null) {
            int daysBetweenPaymentAndCurrent = (int) ChronoUnit.DAYS.between(quoteRequest.getPaymentDate(), DateUtil.getLocalDateNow(quoteRequest.getTimezoneOffset()));
            if (policyMetaCategoryValue.getDaysAfterFinalPayment() >= 0) {
                if (daysBetweenPaymentAndCurrent > policyMetaCategoryValue.getDaysAfterFinalPayment() || daysBetweenPaymentAndCurrent < 0) {
                    return false;
                }
            } else {
                if (daysBetweenPaymentAndCurrent < policyMetaCategoryValue.getDaysAfterFinalPayment() || daysBetweenPaymentAndCurrent > 0) {
                    return false;
                }
            }
        }
        if (policyMetaCategoryValue.getMinAge() != null || policyMetaCategoryValue.getMaxAge() != null) {
            for (GenericTraveler traveler : quoteRequest.getTravelers()) {
                if ((policyMetaCategoryValue.getMinAge() != null && traveler.getAge() < policyMetaCategoryValue.getMinAge())
                        || (policyMetaCategoryValue.getMaxAge() != null && traveler.getAge() > policyMetaCategoryValue.getMaxAge())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean checkDepositAndPaymentGroup(PolicyMetaCategory policyMetaCategory) {
        return Objects.equals(policyMetaCategory.getCategory().getCode(), CategoryCodes.CANCEL_FOR_ANY_REASON) ||
                Objects.equals(policyMetaCategory.getCategory().getCode(), CategoryCodes.PRE_EX_WAIVER) ||
                Objects.equals(policyMetaCategory.getCategory().getCode(), CategoryCodes.CANCEL_FOR_WORK_REASONS) ||
                Objects.equals(policyMetaCategory.getCategory().getCode(), CategoryCodes.PRE_EX_WAIVER_ON_TRIP);
    }

    /**
     * @return policy meta category value for selected value.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public PolicyMetaCategoryValue findCategoryUpsaleValueForSelectedValue(String selectedValue, PolicyMetaCategory policyMetaCategory, QuoteRequest quoteRequest) {
        List<PolicyMetaCategoryValue> categoryValues = policyMetaCategory.getValues();
        // for valueType = other(nan) search value as string and within apiValues
        if (CollectionUtils.isNotEmpty(categoryValues) && categoryValues.get(0).getValueType() == ValueType.NAN && selectedValue != null) {
            List<PolicyMetaCategoryValue> foundValues = categoryValues.stream()
                    .filter(categoryValue -> StringUtils.isNoneBlank(categoryValue.getApiValue())
                            && StringUtils.equals(StringUtils.trim(categoryValue.getApiValue()), StringUtils.trim(selectedValue)))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(foundValues)) {
                return foundValues.get(0);
            }
        }
        PolicyMetaCategoryValue currentValue = policyMetaCategoryValueService.getFirstAcceptedCategoryValue(categoryValues, quoteRequest);
        if (!(PolicyMetaCategoryValue.isEmptyValue(selectedValue)) && categoryValues.size() > 1) {
            currentValue = policyMetaCategory.getValues().get(1);
        }
        Integer selectedIntVal = NumberUtils.toInt(selectedValue);
        selectedIntVal = getValueByTypeForSelected(selectedIntVal, policyMetaCategory, quoteRequest);
        for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
            String val = StringUtils.defaultString(policyMetaCategoryValue.getValue()).trim().toUpperCase();
            Integer categoryIntVal = policyMetaCategoryValue.getValueByType(policyMetaCategory.getCategory().getValueType(), quoteRequest.getTripCostPerTraveler());
            if (val.equals(selectedValue) || (selectedIntVal.equals(categoryIntVal) ||
                    (categoryIntVal != null && selectedIntVal.compareTo(categoryIntVal) < 0)) && selectedIntVal != 0) {
                currentValue = policyMetaCategoryValue;
                break;
            }
        }
        return currentValue;
    }

    private Integer getValueByTypeForSelected(Integer selectedIntVal, PolicyMetaCategory policyMetaCategory, QuoteRequest quoteRequest) {
        Integer valueByType = null;
        for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategory.getValues()) {
            if (Objects.equals(policyMetaCategoryValue.getIntValue(), selectedIntVal)) {
                valueByType = policyMetaCategoryValue.getValueByType(policyMetaCategory.getCategory().getValueType(), quoteRequest.getTripCostPerTraveler());
            }
        }
        return valueByType != null ? valueByType : selectedIntVal;
    }

    /**
     * @return upsale value for selected value.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public String findUpsaleValueForSelectedValue(String selectedValue, PolicyMetaCategory policyMetaCategory, QuoteRequest quoteRequest) {
        CategoryValueSuperclass currentValue = findCategoryUpsaleValueForSelectedValue(selectedValue, policyMetaCategory, quoteRequest);
        return currentValue.getValue().trim().toUpperCase();
    }

    /**
     * @return policy meta category values to be displayed on
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Map<String, PolicyMetaCategoryValue> getCategoryUpsaleValuesFromProduct(PolicyMeta policyMeta, Product product, QuoteRequest quoteRequest) {
        Map<String, PolicyMetaCategoryValue> upsaleValues = new HashMap<>();
        for (Map.Entry<String, String> productUpsalesEntry : product.getUpsaleValueMap().entrySet()) {
            PolicyMetaCategory policyMetaCategory = policyMetaCategoryService.getPolicyMetaCategory(policyMeta, productUpsalesEntry.getKey(), quoteRequest);
            PolicyMetaCategoryValue policyMetaCategoryValue = findCategoryUpsaleValueForSelectedValue(productUpsalesEntry.getValue(), policyMetaCategory, quoteRequest);
            upsaleValues.put(productUpsalesEntry.getKey(), policyMetaCategoryValue);
        }
        return upsaleValues;
    }

    /**
     * @return policy metas that contains categories and upsales.
     */
    @Transactional(readOnly = true)
    public List<PolicyMetaQuoteRequestPair> getPolicyMetaQuoteRequestList(QuoteRequest quoteRequest) {
        List<PolicyMetaQuoteRequestPair> policyMetas = new CopyOnWriteArrayList<>();
        clients.values().parallelStream().forEach(api -> {
            // get policy metas for quote request
            List<PolicyMeta> apiPolicyMetas = getPolicyMetas(quoteRequest, api.getVendorCode());
            apiPolicyMetas.parallelStream().forEach(apiPolicyMeta -> {
                QuoteRequest quoteRequestWithUpsaleForPolicy = getQuoteRequestWithUpsaleForPolicy(apiPolicyMeta, quoteRequest);
                if (quoteRequestWithUpsaleForPolicy != null) {
                    policyMetas.add(new PolicyMetaQuoteRequestPair(apiPolicyMeta, quoteRequestWithUpsaleForPolicy));
                }
            });
        });
        return policyMetas;
    }

    /**
     * @return policy metas from includePolicies that contains categories and upsales.
     */
    @Transactional(readOnly = true)
    public List<PolicyMetaQuoteRequestPair> getPolicyMetaQuoteRequestList(QuoteRequest quoteRequest, List<String> includedPolicies) {
        List<PolicyMetaQuoteRequestPair> policyMetas = new ArrayList<>();
        for (InsuranceVendorApi api : clients.values()) {
            List<PolicyMeta> apiPolicyMetas = getPolicyMetas(quoteRequest, api.getVendorCode());
            for (PolicyMeta apiPolicyMeta : apiPolicyMetas) {
                if (!includedPolicies.contains(apiPolicyMeta.getUniqueCode())) {
                    continue;
                }
                QuoteRequest quoteRequestWithUpsaleForPolicy = getQuoteRequestWithUpsaleForPolicy(apiPolicyMeta, quoteRequest);
                if (quoteRequestWithUpsaleForPolicy != null) {
                    policyMetas.add(new PolicyMetaQuoteRequestPair(apiPolicyMeta, quoteRequestWithUpsaleForPolicy));
                }
            }
        }
        return policyMetas;
    }

    @Transactional(readOnly = true)
    public QuoteRequest getQuoteRequestWithUpsaleForPolicy(PolicyMeta policyMeta, QuoteRequest quoteRequest) {
        return getQuoteRequestWithUpsaleForPolicy(policyMeta, quoteRequest, true);
    }

    @Transactional(readOnly = true)
    public QuoteRequest getQuoteRequestWithUpsaleForPolicy(PolicyMeta policyMeta, QuoteRequest quoteRequest,
                                                           boolean isSliderValue) {
        return getQuoteRequestWithUpsaleForPolicy(policyMeta, quoteRequest, isSliderValue, false);
    }

    /**
     * @return quote request that contains policy upsales.
     */
    @Transactional(readOnly = true)
    public QuoteRequest getQuoteRequestWithUpsaleForPolicy(PolicyMeta policyMeta, QuoteRequest quoteRequest,
                                                           boolean isSliderValue, boolean ignoreConditionals) {
        if (policyMeta == null || quoteRequest == null) {
            return null;
        }
        if (!isPolicyContainsCategory(policyMeta, quoteRequest)) {
            return null;
        }
        Map<String, String> unionCategories = quoteRequest.getUnionCategoriesFromQuoteRequest(isSliderValue);
        QuoteRequest quoteRequestForCurrentPolicy = QuoteRequest.newInstance(quoteRequest);
        //remove all 0 sliders from request
        quoteRequestForCurrentPolicy.setSliderCategories(quoteRequestForCurrentPolicy.getCleanSliderCategories());

        List<PolicyMetaCategory> policyMetaCategoriesByRestrictions = policyMetaCategoryService.getPolicyMetaCategories(policyMeta.getId(), quoteRequest);

        //update upsale values in request for product regarding selected filters
        for (Map.Entry<String, String> categoryEntry : unionCategories.entrySet()) {
            String categoryCode = categoryEntry.getKey();
            Category category = categoryCacheService.getCategory(categoryCode);
            if (category.getType() == Category.CategoryType.CONDITIONAL) {
                if (ignoreConditionals) {
                    continue;
                }
                Binding binding = new Binding();
                GroovyShell groovyShell = new GroovyShell(binding);
                ConditionalCategoryUtil conditionalCategoryUtil = new ConditionalCategoryUtil(quoteRequest, policyMetaCategoriesByRestrictions, this);
                binding.setVariable("util", conditionalCategoryUtil);
                groovyShell.evaluate(category.getCategoryCondition());
                for (Map.Entry<String, String> categoryUpsaleEntry : conditionalCategoryUtil.getUpsaleValueMap().entrySet()) {
                    String key = categoryUpsaleEntry.getKey();
                    String value = categoryUpsaleEntry.getValue();
                    if (quoteRequestForCurrentPolicy.getUpsaleValues().containsKey(key)) {
                        setHighestUpsaleValue(quoteRequestForCurrentPolicy.getUpsaleValues(), value, key, policyMeta.getId(), quoteRequest);
                    } else {
                        quoteRequestForCurrentPolicy.getUpsaleValues().put(key, value);
                    }
                }
            } else {
                for (PolicyMetaCategory policyMetaCategory : policyMetaCategoriesByRestrictions) {
                    if (!Objects.equals(category.getId(), policyMetaCategory.getCategory().getId())) {
                        continue;
                    }
                    if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.UP_SALE) {
                        String upsaleValue = findUpsaleValueForSelectedValue(categoryEntry.getValue(), policyMetaCategory, quoteRequest);
                        if (quoteRequestForCurrentPolicy.getUpsaleValues().containsKey(categoryEntry.getKey())) {
                            setHighestUpsaleValue(quoteRequestForCurrentPolicy.getUpsaleValues(), upsaleValue, categoryEntry.getKey(), policyMeta.getId(), quoteRequest);
                        } else {
                            quoteRequestForCurrentPolicy.getUpsaleValues().put(category.getCode(), upsaleValue);
                        }
                    }
                    break;
                }
            }
        }
        updateQuoteRequestUsingApiValues(quoteRequest, policyMeta);
        return quoteRequestForCurrentPolicy;
    }

    private void updateQuoteRequestUsingApiValues(QuoteRequest quoteRequest, PolicyMeta policyMeta) {
        quoteRequest.getUpsaleValues().forEach((s, s2) -> quoteRequest.getUpsaleValues().put(s, policyMetaCategoryValueService.getApiValue(policyMeta.getId(), s, s2, quoteRequest)));
    }

    /**
     * Select highest upsale value if quote request already contains upsale before.
     */
    private void setHighestUpsaleValue(Map<String, String> quoteRequestUpsaleValues, String value, String key, Long policyMetaId, QuoteRequest quoteRequest) {
        int conditionalUpsale = NumberUtils.toInt(value, -1);
        String quoteRequestUpsaleValue = quoteRequestUpsaleValues.get(key);
        int quoteRequestUpsale = NumberUtils.toInt(quoteRequestUpsaleValue, -1);
        if (conditionalUpsale != -1 && quoteRequestUpsale != -1 && conditionalUpsale > quoteRequestUpsale) {
            quoteRequestUpsaleValues.put(key, value);
        } else {
            List<PolicyMetaCategoryValue> policyMetaCategoryValues = policyMetaCategoryValueService.getSortedCategoryValues(policyMetaId, key, quoteRequest);
            Integer valueIndex = null;
            Integer quoteRequestUpsaleValueIndex = null;
            for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategoryValues) {
                if (valueIndex == null && policyMetaCategoryValue.getValue().equals(value)) {
                    valueIndex = policyMetaCategoryValues.indexOf(policyMetaCategoryValue);
                }
                if (quoteRequestUpsaleValueIndex == null && policyMetaCategoryValue.getValue().equals(quoteRequestUpsaleValue)) {
                    quoteRequestUpsaleValueIndex = policyMetaCategoryValues.indexOf(policyMetaCategoryValue);
                }
                if (valueIndex != null && quoteRequestUpsaleValueIndex != null) {
                    if (valueIndex > quoteRequestUpsaleValueIndex) {
                        quoteRequestUpsaleValues.put(key, value);
                    }
                    break;
                }
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isPolicyHasAndSupportsCancellation(Long policyMetaId) {
        PolicyMeta policyMeta = getPolicyMetaFromParam(policyMetaId);
        boolean policyHasCancellation = false;
        List<PolicyMetaCategory> policyMetaCategories = policyMeta.getPolicyMetaCategories();
        for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
            if (policyMetaCategory.getCategory().getCode().equals(CategoryCodes.TRIP_CANCELLATION)) {
                policyHasCancellation = true;
                break;
            }
        }
        return policyHasCancellation && policyMeta.isSupportsZeroCancellation();
    }

    @Transactional(readOnly = true)
    public boolean isPolicySupportsPlanType(Long policyMetaId, PlanType planType) {
        PolicyMeta policyMeta = getPolicyMetaFromParam(policyMetaId);
        boolean hasTripCancellation = isPolicyHasCategory(policyMeta, CategoryCodes.TRIP_CANCELLATION);
        // comprehensive plan type requires trip cancellation category
        if (planType.getId() == PlanType.COMPREHENSIVE.getId()) {
            return hasTripCancellation;
        }
        // limited plan type requires non cancellation policy or support zero/minimal trip cost
        return !hasTripCancellation || policyMeta.isSupportsZeroCancellation() || policyMeta.hasMinimalTripCost();
    }

    private boolean isPolicyHasCategory(PolicyMeta policyMeta, String code) {
        return policyMeta.getPolicyMetaCategories().stream()
                .anyMatch(policyMetaCategory -> StringUtils.equals(policyMetaCategory.getCategory().getCode(), code));
    }

    private PolicyMeta getPolicyMetaFromParam(Long policyMetaId) {
        if (policyMetaId == null) {
            throw new NullPointerException("policyMetaId is null");
        }
        Optional<PolicyMeta> policyMeta = Optional.ofNullable(getCached(policyMetaId));
        return policyMeta.orElseThrow(() -> new NullPointerException("policyMeta is null"));
    }

    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void deletePolicyMeta(Long policyMetaId) {
        policyMetaRepository.delete(policyMetaId);
    }

    @Transactional(readOnly = true)
    public Map<String, PolicyMetaCategory> getContentCategoriesWithCertificateText(PolicyMeta policyMetaParam, QuoteRequest quoteRequest) {
        Map<String, PolicyMetaCategory> categoriesWithCertificateText = new HashMap<>();
        List<PolicyMetaCategory> policyMetaCategories = policyMetaCategoryService.getPolicyMetaCategories(policyMetaParam.getId(), quoteRequest);
        for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
            if (categoryService.hasTemplate(policyMetaCategory)) {
                applyTemplate(policyMetaCategory, quoteRequest.getTripCostPerTraveler());
            }
            //TODO remove ValuesAfterPolicyMetaConditions from policy meta category
            fillValuesAfterPolicyMetaConditions(policyMetaCategory, quoteRequest);
            categoriesWithCertificateText.put(policyMetaCategory.getCategory().getCode(), policyMetaCategory);
        }
        return categoriesWithCertificateText;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void applyTemplate(PolicyMetaCategory policyMetaCategory, BigDecimal tripCost) {
        if (tripCost == null || policyMetaCategory.getValues().stream().allMatch(value -> CollectionUtils.isEmpty(value.getSubcategoryValuesList()))) {
            return;
        }
        Category category = policyMetaCategory.getCategory();
        List<PolicyMetaCategoryValue> values = policyMetaCategory.getValues();
        List<Subcategory> subcategories = subcategoryService.getSubcategories(category.getId());
        if (values.size() == 1 && !PolicyMetaCategoryValue.isEmptyValue(values.get(0).getValue())) {
            // skip template for value type OTHER (example: "not selected" value)
            if (values.get(0).getValueType() == ValueType.NAN) {
                return;
            }
            String velocityResult = VelocityHelper.getInstance().replace(category,
                    values.get(0), tripCost, subcategories);
            policyMetaCategory.getValues().get(0).setCaption(velocityResult);
        } else {
            policyMetaCategory.getValues().stream()
                    // skip template for value type OTHER (example: "not selected" value)
                    .filter(policyMetaCategoryValue -> policyMetaCategoryValue.getValueType() != ValueType.NAN)
                    .forEach(value -> value.setCaption(VelocityHelper.getInstance().replace(category, value, tripCost, subcategories)));
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void fillValuesAfterPolicyMetaConditions(PolicyMetaCategory policyMetaCategory, QuoteRequest quoteRequest) {
        List<PolicyMetaCategoryValue> values = policyMetaCategory.getValues().stream().filter(
                categoryValue -> checkPolicyMetaCategoryValueByConditions(categoryValue, quoteRequest, true)
        ).collect(Collectors.toList());
        if (values.size() == 1 && PolicyMetaCategoryValue.isEmptyValue(values.get(0).getValue())) {
            policyMetaCategory.setValuesAfterConditions(Collections.emptyList());
        } else {
            Collections.sort(values);
            policyMetaCategory.setValuesAfterConditions(values);
        }
    }

    public List<PolicyMetaCategory> getUpsalesFromPolicy(PolicyMeta policyMeta, QuoteRequest quoteRequest) {
        List<Category> categoriesByPlanType = categoryService.getCategoriesByPlanType(quoteRequest.getPlanType());
        List<PolicyMetaCategory> policyMetaCategories = policyMetaCategoryService.getPolicyMetaCategories(policyMeta.getId(), quoteRequest).stream().filter(
                policyMetaCategory -> (categoriesByPlanType.contains(policyMetaCategory.getCategory()) && policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.UP_SALE)).collect(Collectors.toList());
        for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
            List<PolicyMetaCategoryValue> values = policyMetaCategoryValueService.getAllAcceptedCategoryValue(policyMetaCategory.getValues(), quoteRequest);
            Collections.sort(values);
            policyMetaCategory.setValuesAfterConditions(values);
        }
        return policyMetaCategories;
    }

    @Transactional(readOnly = true)
    public List<PolicyMetaPackage> getPackagesFilteredByRestrictions(PolicyMeta policyMeta, QuoteRequest quoteRequest) {
        List<PolicyMetaPackage> result = new ArrayList<PolicyMetaPackage>();
        if (policyMeta != null) {
            policyMeta = getCached(policyMeta.getId());
            result = policyMeta.getPolicyMetaPackages().stream().filter(policyMetaPackage -> restrictionService.isValid(quoteRequest, policyMetaPackage.getPolicyMetaPackageRestrictions())).collect(Collectors.toList());
        }
        return result;
    }

    @Transactional(readOnly = true)
    public PolicyMetaPackage getPackageByCodeFilteredByRestrictions(PolicyMeta policyMeta, QuoteRequest quoteRequest, String packageCode) {
        policyMeta = getCached(policyMeta.getId());
        return policyMeta.getPolicyMetaPackages().stream().filter(policyMetaPackage -> restrictionService.isValid(quoteRequest, policyMetaPackage.getPolicyMetaPackageRestrictions()))
                .filter(policyMetaPackage -> StringUtils.equals(packageCode, policyMetaPackage.getCode())).findFirst().orElseThrow(() -> new RuntimeException("package for policy not found"));
    }

    public PolicyMetaPackage getPolicyMetaPackageByCode(String packageCode, PolicyMeta policyMeta) {
        return policyMeta.getPolicyMetaPackages().stream()
                .filter(policyMetaPackage -> StringUtils.equals(policyMetaPackage.getCode(), packageCode))
                .findFirst().orElseThrow(() -> new RuntimeException("package for policy not found"));
    }

    public PolicyMetaCategoryValue getDefaultValueForCategory(PolicyMetaCategory policyMetaCategory, QuoteRequest quoteRequest) {
        return policyMetaCategoryValueService.getFirstAcceptedCategoryValue(policyMetaCategory.getValues(), quoteRequest);
    }

    public void enablePackage(PolicyMeta policyMeta, QuoteRequest quoteRequest, String packageCode) {
        PolicyMetaPackage pmPackage = policyMeta.getPolicyMetaPackages().stream()
                .filter(policyMetaPackage -> StringUtils.equals(policyMetaPackage.getCode(), packageCode))
                .findFirst().orElseThrow(() -> new RuntimeException("package for policy not found"));
        pmPackage.getPolicyMetaPackageValues().forEach(value -> quoteRequest.getCategories().put(value.getPolicyMetaCategory().getCategory().getCode(), value.getValue()));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void disablePackage(PolicyMeta policyMeta, QuoteRequest quoteRequest, String packageCode) {
        PolicyMetaPackage pmPackage = policyMeta.getPolicyMetaPackages().stream()
                .filter(policyMetaPackage -> StringUtils.equals(policyMetaPackage.getCode(), packageCode))
                .findFirst().orElseThrow(() -> new RuntimeException("package for policy not found"));
        pmPackage.getPolicyMetaPackageValues().forEach(value -> {
            PolicyMetaCategoryValue defaultValue = policyMetaCategoryValueService.getFirstAcceptedCategoryValue(value.getPolicyMetaCategory().getValues(), quoteRequest);
            quoteRequest.getCategories().put(value.getPolicyMetaCategory().getCategory().getCode(), defaultValue.getValue());
        });
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<PolicyMeta> getPolicyMetasForVendor(String vendorCode){
        return policyMetaCacheService.getPolicyMetas().values()
                .parallelStream()
                .filter(policyMeta -> StringUtils.equals(policyMeta.getVendor().getCode(), vendorCode))
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PolicyMeta getCached(Long id) {
        return policyMetaCacheService.getPolicyMetas().get(id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PolicyMeta getCached(PolicyMeta policyMeta) {
        return policyMetaCacheService.getPolicyMetas().get(policyMeta.getId());
    }
}