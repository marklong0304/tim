package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.json.JsonCategory;
import com.travelinsurancemaster.model.dto.json.JsonSearchProduct;
import com.travelinsurancemaster.model.dto.system.SystemSettings;
import com.travelinsurancemaster.model.webservice.common.Product;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.system.SystemSettingsService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Chernov Artur on 29.04.15.
 */

@Service
@Transactional(readOnly = true)
public class SearchService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private QuoteRequestService quoteRequestService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    public List<JsonSearchProduct> getJsonResponses(List<Product> includedPolicies, List<Product> excludePolicies, Product bestPolicy, QuoteRequest quoteRequest, boolean bestIncluded) {
        List<JsonSearchProduct> jsonResponses = new ArrayList<>();
        SystemSettings systemSettings = systemSettingsService.getDefault();
        if (bestPolicy != null) {
            jsonResponses.add(getJsonSearchResponse(bestPolicy, quoteRequest, bestIncluded, true, systemSettings));
        }
        for (Product policy : includedPolicies) {
            JsonSearchProduct jsonResponse = getJsonSearchResponse(policy, quoteRequest, true, false, systemSettings);
            jsonResponses.add(jsonResponse);
        }
        for (Product policy : excludePolicies) {
            JsonSearchProduct jsonResponse = getJsonSearchResponse(policy, quoteRequest, false, false, systemSettings);
            jsonResponses.add(jsonResponse);
        }
        return jsonResponses;
    }

    public JsonSearchProduct getJsonSearchResponse(Product policy, QuoteRequest quoteRequest, boolean included, boolean best) {
        return getJsonSearchResponse(policy, quoteRequest, included, best, systemSettingsService.getDefault());
    }

    private JsonSearchProduct getJsonSearchResponse(Product policy, QuoteRequest quoteRequest, boolean included, boolean best, SystemSettings settings) {
        JsonSearchProduct jsonResponse = new JsonSearchProduct(settings);
        PolicyMeta policyMeta = policyMetaService.getCached(policy.getPolicyMeta().getId());
        jsonResponse.setPolicyMetaId(policyMeta.getId());
        jsonResponse.setPolicyMetaName(policyMeta.getDisplayName());
        jsonResponse.setPolicyMetaCode(policy.getPolicyMetaCode().getCode());
        jsonResponse.setPolicyMetaCodeName(policy.getPolicyMetaCode().getName());
        jsonResponse.setTotalPrice(policy.getTotalPrice());
        jsonResponse.setVendorName(policyMeta.getVendor().getName());
        jsonResponse.setVendorCode(policyMeta.getVendor().getCode());
        jsonResponse.setPolicyMetaUniqueCode(policyMeta.getUniqueCode());
        jsonResponse.setCountOfTravelers(quoteRequest.getTravelers().size());
        jsonResponse.setPurchasable(policyMeta.isPurchasable());
        for (JsonSearchProduct.InnerCategory category : jsonResponse.getCategories()) {
            PolicyMetaCategory policyMetaCategory = policyMetaCategoryService.getPolicyMetaCategory(policyMeta.getId(), category.getCategoryCode(), quoteRequest);
            if (policyMetaCategory != null) {
                if (categoryService.hasTemplate(policyMetaCategory)) {
                    this.policyMetaService.applyTemplate(policyMetaCategory, quoteRequest.getTripCostPerTraveler());
                }
                category.setCategoryName(policyMetaCategory.getCategory().getName());
                category.setCategoryDescription(policyMetaCategory.getDescription());
                String currentCategoryValueFromQuoteRequest = policy.getUpsaleValueMap().get(policyMetaCategory.getCategory().getCode());
                PolicyMetaCategoryValue resultCategoryValue = policyMetaService.findCategoryUpsaleValueForSelectedValue(currentCategoryValueFromQuoteRequest, policyMetaCategory, quoteRequest);
                QuoteRequest requestToApi = quoteRequestService.getQuoteRequestWithCancellation(quoteRequest, policyMeta);
                if (GroupService.isCancellationGroup(policyMetaCategory.getCategory()) && requestToApi.isZeroCost()) {
//                    category.setCategoryValue("-");
//                    category.setCategoryCaption("-");
                    category.setCategoryValue("No");
                    category.setCategoryCaption("No");
                } else {
                    if (resultCategoryValue == null || quoteRequestService.checkNoTripCancellation(quoteRequest, resultCategoryValue)) {
//                        category.setCategoryValue("-");
//                        category.setCategoryCaption("-");
                        category.setCategoryValue("No");
                        category.setCategoryCaption("No");
                    } else {
                        category.setCategoryValue(resultCategoryValue);
                    }
                }
            }
        }
        jsonResponse.setIncluded(included);
        jsonResponse.setBest(best);
        return jsonResponse;
    }

    public Map<Long, JsonCategory> getCatalogCategoriesValuesFromProducts(List<Product> products, List<Product> baseProducts, List<Product> productsWithCancellation, QuoteRequest quoteRequest) {
        List<Category> catalogCategories = categoryService.getByCategoryType(Category.CategoryType.CATALOG);
        Map<Category, Integer> maxCatalogsValueMap = getMaxCatalogsValueMapFromProducts(products, catalogCategories, quoteRequest);
        Map<Category, HashSet<PolicyMetaCategoryValue>> catalogsValuesUnderMaxValueMap = getCatalogsValuesUnderMaxValueMapFromProducts(baseProducts, maxCatalogsValueMap, quoteRequest);
        Map<Category, HashSet<PolicyMetaCategoryValue>> catalogsValuesUnderMaxValueMapWithCancellation = catalogsValuesUnderMaxValueMap;
        if (!quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
            Map<Category, Integer> maxCatalogsValueMapWithCancellation = getMaxCatalogsValueMapFromProducts(productsWithCancellation, catalogCategories, quoteRequest);
            catalogsValuesUnderMaxValueMapWithCancellation = getCatalogsValuesUnderMaxValueMapFromProducts(baseProducts, maxCatalogsValueMapWithCancellation, quoteRequest);
        }
        Map<Long, JsonCategory> results = new HashMap<>();
        for (Category category : catalogCategories) {
            JsonCategory jsonCategory = new JsonCategory(category);
            if (CategoryService.isLookBackPeriod(category)) {
                jsonCategory.put(catalogsValuesUnderMaxValueMapWithCancellation.get(category), category.getValueType(), quoteRequest.getTripCostPerTraveler());
            } else {
                jsonCategory.put(catalogsValuesUnderMaxValueMap.get(category), category.getValueType(), quoteRequest.getTripCostPerTraveler());
            }
            results.put(jsonCategory.getId(), jsonCategory);
        }
        return results;
    }

    private Map<Category, Integer> getMaxCatalogsValueMapFromProducts(List<Product> products, List<Category> categories, QuoteRequest quoteRequest) {
        Map<Category, Integer> maxCatalogsValueMap = new HashMap<>();
        for (Category category : categories) {
            maxCatalogsValueMap.put(category, !CategoryService.isLookBackPeriod(category) ? 0 : Integer.MAX_VALUE);
        }
        for (Product product : products) {
            PolicyMeta policyMeta = policyMetaService.getCached(product.getPolicyMeta().getId());
            // get catalog categories from policy meta by restrictions
            List<PolicyMetaCategory> policyMetaCategoriesByRestrictions =  policyMetaCategoryService.getPolicyMetaCategories(policyMeta.getId(), quoteRequest, categories);
            for (PolicyMetaCategory policyMetaCategory : policyMetaCategoriesByRestrictions) {
                Category category = policyMetaCategory.getCategory();
                Integer maxCategoryValue = getMaxCatalogCategoryValueFromPolicyMetaCategory(policyMetaCategory, quoteRequest);
                if (!CategoryService.isLookBackPeriod(category) ? maxCatalogsValueMap.get(category) < maxCategoryValue : maxCatalogsValueMap.get(category) > maxCategoryValue) {
                    maxCatalogsValueMap.put(category, maxCategoryValue);
                }
            }
        }
        return maxCatalogsValueMap;
    }

    private Integer getMaxCatalogCategoryValueFromPolicyMetaCategory(PolicyMetaCategory policyMetaCategory, QuoteRequest quoteRequest) {
        Integer maxCategoryValue = !CategoryService.isLookBackPeriod(policyMetaCategory.getCategory()) ? 0 : Integer.MAX_VALUE;
        for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategory.getValues()) {
            Integer val = policyMetaCategoryValue.getValueByType(policyMetaCategory.getCategory().getValueType(), quoteRequest.getTripCostPerTraveler());
            if (val != null && (!CategoryService.isLookBackPeriod(policyMetaCategory.getCategory()) ? val > maxCategoryValue : val < maxCategoryValue)) {
                maxCategoryValue = val;
            }
        }
        return maxCategoryValue;
    }

    private Map<Category, HashSet<PolicyMetaCategoryValue>> getCatalogsValuesUnderMaxValueMapFromProducts(List<Product> products, Map<Category, Integer> maxCatalogsValueMap, QuoteRequest quoteRequest) {
        Set<Category> catalogCategories = maxCatalogsValueMap.keySet();
        Map<Category, HashSet<PolicyMetaCategoryValue>> catalogsValuesUnderMaxValueMap = new HashMap<>();
        for (Category category : catalogCategories) {
            catalogsValuesUnderMaxValueMap.put(category, new HashSet<>());
        }
        for (Product product : products) {
            PolicyMeta policyMeta = policyMetaService.getCached(product.getPolicyMeta().getId());
            List<PolicyMetaCategory> policyMetaCategoriesByRestrictions =  policyMetaCategoryService.getPolicyMetaCategories(policyMeta.getId(), quoteRequest);
            for (PolicyMetaCategory policyMetaCategory : policyMetaCategoriesByRestrictions) {
                Category category = policyMetaCategory.getCategory();
                if (catalogCategories.contains(category)) {
                    Set<PolicyMetaCategoryValue> categoryValuesUnderMaxValue = getCatalogCategoryValuesUnderMaxValueFromPolicyMetaCategory(policyMetaCategory, maxCatalogsValueMap.get(category), quoteRequest);
                    catalogsValuesUnderMaxValueMap.get(category).addAll(categoryValuesUnderMaxValue);
                }
            }
        }
        return catalogsValuesUnderMaxValueMap;
    }

    private Set<PolicyMetaCategoryValue> getCatalogCategoryValuesUnderMaxValueFromPolicyMetaCategory(PolicyMetaCategory policyMetaCategory, Integer maxValue, QuoteRequest quoteRequest) {
        Set<PolicyMetaCategoryValue> categoryValuesUnderMaxValue = new HashSet<>();
        for (PolicyMetaCategoryValue policyMetaCategoryValue : policyMetaCategory.getValues()) {
            Integer val = policyMetaCategoryValue.getValueByType(policyMetaCategory.getCategory().getValueType(), quoteRequest.getTripCostPerTraveler());
            if (val != null && (!CategoryService.isLookBackPeriod(policyMetaCategory.getCategory()) ? val <= maxValue : val >= maxValue)) {
                categoryValuesUnderMaxValue.add(policyMetaCategoryValue);
            }
        }
        return categoryValuesUnderMaxValue;
    }

    public List<JsonCategory> getCategoriesFromProducts(List<Product> products, List<Product> productsWithCancellation, QuoteRequest
                    quoteRequest, Map<Long, JsonCategory> clearSlidersQuoteRequestCatalogValues) {
        JsonCategory preExWaiver = null;
        JsonCategory lookBackPeriod = null;
        List<JsonCategory> results = new ArrayList<>();
        List<Category> categories = categoryService.getAllDisplayedAsFilter();
        Map<String, String> currentCategoryValues = quoteRequest.getCategories();
        boolean checkPolicyMetaCategoryRestrictionType = quoteRequest.getPaymentDate() != null && quoteRequest.getDepositDate() != null;
        // add categories to results
        Map<Category, Integer> categoriesFromProductsHash = getCategoriesFromProductsHash(products, productsWithCancellation, quoteRequest, checkPolicyMetaCategoryRestrictionType);
        for (Category category : categories) {
            JsonCategory jsonCategory = new JsonCategory(category);
            if (Objects.equals(category.getCode(), CategoryCodes.PRE_EX_WAIVER)) {
                preExWaiver = jsonCategory;
            } else if (Objects.equals(category.getCode(), CategoryCodes.LOOK_BACK_PERIOD)) {
                lookBackPeriod = jsonCategory;
            }

            if (currentCategoryValues.containsKey(category.getCode())) {
                jsonCategory.setChecked(true);
                if (category.getType() == Category.CategoryType.CATALOG) {
                    jsonCategory.setCurrentValue(NumberUtils.toLong(currentCategoryValues.get(category.getCode())));
                } else {
                    jsonCategory.setCurrentValue(-1L);
                }
            }

            if (category.getType() == Category.CategoryType.CATALOG) {
                JsonCategory catalogCategory = clearSlidersQuoteRequestCatalogValues.get(category.getId());
                if (catalogCategory != null) {
                    jsonCategory.put(catalogCategory.getValues());
                }
            }
            if (category.getType() != Category.CategoryType.CONDITIONAL) {
                fillCountAfterEnabledAndDisabled(products, productsWithCancellation, quoteRequest, category, jsonCategory, categoriesFromProductsHash);
            }
            results.add(jsonCategory);
        }
        if (preExWaiver != null && lookBackPeriod != null) {
            if (preExWaiver.isChecked()) {
                lookBackPeriod.setChecked(false);
                lookBackPeriod.setDisabled(true);
                lookBackPeriod.setCountAfterEnabled(0);
            }
        }
        return results;
    }

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    /**
     * Counts category amounts in policies.
     */
    private void fillCountAfterEnabledAndDisabled(List<Product> currentProducts, List<Product> productsWithCancellation, QuoteRequest quoteRequest,
                                                          Category category, JsonCategory jsonCategory, Map<Category, Integer> categoriesFromProductsHash) {
        // if category is enabled
        if (quoteRequest.getCategories().containsKey(category.getCode())) {
            if (GroupService.isCancellationGroup(category) && !quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
                jsonCategory.setCountAfterEnabled(productsWithCancellation.size());
            } else {
                jsonCategory.setCountAfterEnabled(currentProducts.size());
            }
            jsonCategory.setDisabled(false);
            return;
        }

        if (categoriesFromProductsHash.containsKey(category)){
            jsonCategory.setCountAfterEnabled(categoriesFromProductsHash.get(category));
            jsonCategory.setDisabled(false);
        }
    }

    private Map<Category, Integer> getCategoriesFromProductsHash(List<Product> products, List<Product> productsWithCancellation, QuoteRequest quoteRequest, boolean checkPolicyMetaCategoryRestrictionType) {
        Map<Category, Integer> categoryUsingInProducts = new HashMap<>();
        for (Product product : products) {
            List<PolicyMetaCategory> policyMetaCategories = policyMetaCategoryService.getPolicyMetaCategories(product.getPolicyMeta().getId(), quoteRequest);
            for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
                if (GroupService.isCancellationGroup(policyMetaCategory.getCategory()) && !quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
                    continue;
                }
                if (policyMetaService.checkPolicyMetaConditions(policyMetaCategory, quoteRequest,
                        !(!checkPolicyMetaCategoryRestrictionType && policyMetaService.checkDepositAndPaymentGroup(policyMetaCategory)))) {
                    categoryUsingInProducts.merge(policyMetaCategory.getCategory(), 1, (a, b) -> a + b);
                }
            }
        }
        if (!quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
            for (Product product : productsWithCancellation) {
                List<PolicyMetaCategory> policyMetaCategories = policyMetaCategoryService.getPolicyMetaCategories(product.getPolicyMeta().getId(), quoteRequest);
                for (PolicyMetaCategory policyMetaCategory : policyMetaCategories) {
                    if (GroupService.isCancellationGroup(policyMetaCategory.getCategory()) && !quoteRequest.getCategories().containsKey(CategoryCodes.TRIP_CANCELLATION)) {
                        if (policyMetaService.checkPolicyMetaConditions(policyMetaCategory, quoteRequest,
                                !(!checkPolicyMetaCategoryRestrictionType && policyMetaService.checkDepositAndPaymentGroup(policyMetaCategory)))) {
                            categoryUsingInProducts.merge(policyMetaCategory.getCategory(), 1, (a, b) -> a + b);
                        }
                    }
                }
            }
        }
        return categoryUsingInProducts;
    }


    public CategoryReduceResult updateCategoryReduceResult(CategoryReduceResult result) {
        if (MapUtils.isEmpty(result.getQuoteRequest().getCategories())) {
            result.setReduced(false);
            return result;
        }
        for (String categoryCode : CategoryReduceResult.CATEGORIES_FOR_REDUCE) {
            if (result.getQuoteRequest().getCategories().containsKey(categoryCode)) {
                result.getReducedCategories().add(categoryService.getByCode(categoryCode));
                result.getQuoteRequest().getCategories().remove(categoryCode);
                return result;
            }
        }
        result.setReduced(false);
        return result;
    }

}
