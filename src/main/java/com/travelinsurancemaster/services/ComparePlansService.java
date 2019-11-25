package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.GroupNames;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaCategoryContent;
import com.travelinsurancemaster.model.dto.json.*;
import com.travelinsurancemaster.model.dto.system.SystemSettings;
import com.travelinsurancemaster.model.webservice.common.CompareResult;
import com.travelinsurancemaster.model.webservice.common.Product;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.cms.CertificateService;
import com.travelinsurancemaster.services.cms.PolicyMetaCategoryContentService;
import com.travelinsurancemaster.services.system.SystemSettingsService;
import com.travelinsurancemaster.util.JsonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Chernov Artur on 19.05.15.
 */
@Service
public class ComparePlansService {

    @Autowired
    private ProductService productService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private QuoteRequestService quoteRequestService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    @Autowired
    private PolicyMetaCategoryContentService policyMetaCategoryContentService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    public JsonCompareResult getJsonComparePlansResult(CompareResult compareResult, QuoteRequest quoteRequest, String bestPlanCode, HttpServletRequest request) {
        JsonCompareResult jsonCompareResult = new JsonCompareResult();
        List<JsonCompareProduct> jsonCompareProducts = getJsonCompareProducts(compareResult.getProducts(), quoteRequest, bestPlanCode, request);
        jsonCompareResult.setProducts(jsonCompareProducts);
        jsonCompareResult.setCategories(compareResult.getCategories());
        jsonCompareResult.setShowCountAfterEnabledFilter(compareResult.isShowCountAfterEnabledFilter());
        jsonCompareResult.setFinished(compareResult.isFinished());
        jsonCompareResult.setJsonGroups(getJsonGroup(quoteRequest));
        jsonCompareResult.setQuoteRequestJson(JsonUtils.getJsonString(quoteRequest));
        jsonCompareResult.setZeroCost(quoteRequest.isZeroCost());
        jsonCompareResult.setPlanType(quoteRequest.getPlanType());
        jsonCompareResult.setDepositAndPaymentDates(quoteRequest.isDepositAndPaymentDates());
        return jsonCompareResult;
    }

    private List<JsonCompareProduct> getJsonCompareProducts(List<Product> products, QuoteRequest quoteRequest, String bestPlanCode, HttpServletRequest request) {
        List<JsonCompareProduct> jsonCompareProducts = new ArrayList<>();
        SystemSettings systemSettings = systemSettingsService.getDefault();
        for (Product product : products) {
            jsonCompareProducts.add(getJsonCompareProduct(product, quoteRequest, bestPlanCode, request, systemSettings));
        }
        return jsonCompareProducts;
    }

    private JsonCompareProduct getJsonCompareProduct(Product product, QuoteRequest quoteRequest, String bestPlanCode, HttpServletRequest request, SystemSettings settings) {
        PolicyMeta policyMeta = policyMetaService.getCached(product.getPolicyMeta());
        JsonCompareProduct jsonCompareProduct = new JsonCompareProduct(settings);
        jsonCompareProduct.setPolicyMetaId(policyMeta.getId());
        jsonCompareProduct.setPolicyMetaName(product.getPolicyMetaCode().getName());
        jsonCompareProduct.setPolicyMetaCode(product.getPolicyMetaCode().getCode());
        jsonCompareProduct.setTotalPrice(product.getTotalPrice());
        jsonCompareProduct.setVendorName(policyMeta.getVendor().getName());
        jsonCompareProduct.setVendorCode(policyMeta.getVendor().getCode());
        jsonCompareProduct.setPolicyMetaUniqueCode(policyMeta.getUniqueCode());
        jsonCompareProduct.setPurchasable(policyMeta.isPurchasable());
        jsonCompareProduct.setBest(StringUtils.equals(bestPlanCode, policyMeta.getUniqueCode()));
        jsonCompareProduct.setCountOfTravelers(quoteRequest.getTravelers().size());
        for (JsonCompareProduct.InnerCategory category : jsonCompareProduct.getCategories()) {
            PolicyMetaCategory policyMetaCategory = policyMetaCategoryService.getPolicyMetaCategory(policyMeta.getId(), category.getCategoryCode(), quoteRequest);
            if (policyMetaCategory != null) {
                if (categoryService.hasTemplate(policyMetaCategory)) {
                    this.policyMetaService.applyTemplate(policyMetaCategory, quoteRequest.getTripCostPerTraveler());
                }
                category.setCategoryName(policyMetaCategory.getCategory().getName());
                category.setCategoryDescription(policyMetaCategory.getDescription());
                String currentCategoryValueFromQuoteRequest = product.getUpsaleValueMap().get(policyMetaCategory.getCategory().getCode());
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
        jsonCompareProduct.setCertificateLink(product.getCertificate() == null ? certificateService.getCertificateUrl(quoteRequest, policyMeta, request) : product.getCertificate());
        QuoteRequest requestToApi = quoteRequestService.getQuoteRequestWithCancellation(quoteRequest, policyMeta);
        jsonCompareProduct.setCategoryValues(getJsonCategoryValueMap(product, policyMeta, quoteRequest, requestToApi.isZeroCost()));
        return jsonCompareProduct;
    }

    private List<JsonGroup> getJsonGroup(QuoteRequest quoteRequest) {
        List<Group> groups = groupService.getGroupsByPlanType(quoteRequest.getPlanType());
        List<JsonGroup> jsonGroups = new ArrayList<>();
        for (Group group : groups) {
            JsonGroup jsonGroup = new JsonGroup(group.getName());
            for (Category category : group.getCategoryList()) {
                jsonGroup.getCategories().add(getJsonCompareCategory(category));
            }
            jsonGroups.add(jsonGroup);
        }
        return jsonGroups;
    }

    private JsonCompareCategory getJsonCompareCategory(Category category) {
        return new JsonCompareCategory(category.getName(), category.getCode());
    }

    private Map<String, JsonCategoryValue> getJsonCategoryValueMap(Product product, PolicyMeta policyMeta, QuoteRequest quoteRequest, boolean isZeroCost) {
        Map<String, JsonCategoryValue> jsonCategoryValueMap = new HashMap<>();
        List<Group> groupList = groupService.getAll();
        for (Group group : groupList) {
            List<Category> categoryList = group.getCategoryList();
            for (Category category : categoryList) {
                JsonCategoryValue jcValue = getJsonCategoryValue(product, policyMeta, group, category, quoteRequest, isZeroCost);
                jsonCategoryValueMap.put(category.getCode(), jcValue);
            }
        }
        return jsonCategoryValueMap;
    }

    private JsonCategoryValue getJsonCategoryValue(Product product, PolicyMeta policyMeta, Group group, Category category, QuoteRequest quoteRequest, boolean isZeroCost) {
        JsonCategoryValue jsonCategoryValue = new JsonCategoryValue(category.getCode(), policyMeta.getId().toString());
        boolean isMinTripCost = quoteRequestService.checkMinTripCost(quoteRequest, policyMeta);
        String categoryValue = StringUtils.EMPTY;
        boolean secondary = false;
        boolean showCertificate = false;
        PolicyMetaCategory policyMetaCategory = policyMetaCategoryService.getPolicyMetaCategory(policyMeta, category.getCode(), quoteRequest);
        if (policyMetaCategory != null && categoryService.hasTemplate(policyMetaCategory)) {
            policyMetaService.applyTemplate(policyMetaCategory, quoteRequest.getTripCostPerTraveler());
        }
        if (policyMetaCategory != null) {
            policyMetaService.fillValuesAfterPolicyMetaConditions(policyMetaCategory, quoteRequest);
        }
        if (policyMetaCategory != null) {
            PolicyMetaCategoryContent content = policyMetaCategoryContentService.getPolicyMetaCategoryContent(policyMeta, category);
            if (content != null && content.getCertificateText() != null && StringUtils.isNoneBlank(content.getCertificateText())) {
                showCertificate = true;
            }
        }
        if ((isZeroCost || isMinTripCost) && StringUtils.equals(GroupNames.Cancellation, group.getName())) {
            if (isZeroCost) {
                categoryValue = policyMetaCategory == null ? "-" : "n/a";
            }
            if (isMinTripCost) {
                if (policyMetaCategory == null) {
                    categoryValue = "-";
                } else {
                    jsonCategoryValue.setShowCertificate(showCertificate);
                    categoryValue = policyMetaCategory.getMinCostValue(policyMeta.getMinimalTripCost()) + '.';
                    List<PolicyMetaCategoryValue> values = policyMetaCategory.getValuesAfterConditions();
                    if (values.size() > 0 && !StringUtils.equals(values.get(0).getValueType().getCaption(), "Other")) {
                        categoryValue += "Due to policy restriction, trip cost must be at least $";
                        categoryValue += policyMeta.getMinimalTripCost();
                    }
                }
            }
            if (policyMetaCategory != null && CollectionUtils.isNotEmpty(policyMetaCategory.getValuesAfterConditions())) {
                secondary = policyMetaCategory.getValuesAfterConditions().get(0).isSecondary();
            }
        } else {
            if (policyMetaCategory == null || CollectionUtils.isEmpty(policyMetaCategory.getValuesAfterConditions())) {
                categoryValue = "-";
            } else {
                jsonCategoryValue.setShowCertificate(showCertificate);
                if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.SIMPLE) {
                    String categoryCode = product.getUpsaleValueMap().get(category.getCode());
                    Optional<PolicyMetaCategoryValue> value = policyMetaCategory.getValuesAfterConditions().stream()
                            .filter(policyMetaCategoryValue -> policyMetaCategoryValue.getValue().equals(categoryCode))
                            .findFirst();
                    categoryValue += value.map(CategoryValueSuperclass::getCaption).orElseGet(() -> policyMetaCategory.getValuesAfterConditions().get(0).getCaption());
                    secondary = value.map(PolicyMetaCategoryValue::isSecondary).orElseGet(() -> policyMetaCategory.getValuesAfterConditions().get(0).isSecondary());
                } else {
                    if (policyMetaCategory.getValuesAfterConditions().get(0).getValueType() == ValueType.NAN) {
                        categoryValue = "Optional";
                    } else {
                        PolicyMetaCategoryValue firstValue = policyMetaCategory.getValuesAfterConditions().get(0);
                        PolicyMetaCategoryValue lastValue = policyMetaCategory.getValuesAfterConditions().get(policyMetaCategory.getValuesAfterConditions().size() - 1);
                        categoryValue = firstValue.getCaption() + " - " + lastValue.getCaption();
                    }
                    secondary = false;
                }
            }
        }
        jsonCategoryValue.setValue(categoryValue);
        jsonCategoryValue.setSecondary(secondary);
        return jsonCategoryValue;
    }
}
