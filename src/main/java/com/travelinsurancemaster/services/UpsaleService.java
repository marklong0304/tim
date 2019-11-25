package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.json.NotificationColor;
import com.travelinsurancemaster.model.dto.json.NotificationDTO;
import com.travelinsurancemaster.model.dto.json.TooltipMessageDTO;
import com.travelinsurancemaster.model.dto.json.UpsaleResponseDTO;
import com.travelinsurancemaster.model.dto.system.SystemSettings;
import com.travelinsurancemaster.model.webservice.common.Product;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.model.webservice.common.Result;
import com.travelinsurancemaster.services.system.SystemSettingsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Artur Chernov
 */

@Service
public class UpsaleService {

    @Autowired
    PolicyMetaService policyMetaService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    PolicyMetaCategoryValueService policyMetaCategoryValueService;

    public UpsaleResponseDTO changePackageStatus(PolicyMeta policyMeta, QuoteRequest quoteRequest, String packageCode, boolean enabled) {
        UpsaleResponseDTO upsaleResponse = new UpsaleResponseDTO();
        if (enabled) {
            policyMetaService.enablePackage(policyMeta, quoteRequest, packageCode);
            upsaleResponse.getNotifications().add(new NotificationDTO(policyMetaService.getPackageByCodeFilteredByRestrictions(policyMeta, quoteRequest, packageCode).getName(), NotificationColor.GREEN));
        } else {
            policyMetaService.disablePackage(policyMeta, quoteRequest, packageCode);
            upsaleResponse.getNotifications().add(new NotificationDTO(policyMetaService.getPackageByCodeFilteredByRestrictions(policyMeta, quoteRequest, packageCode).getName(), NotificationColor.RED));
        }
        Optional<Product> product = Optional.ofNullable(productService.getProduct(policyMeta.getUniqueCode(), quoteRequest, true, true));
        if (!product.isPresent()) {
            upsaleResponse.setError("No product based on your request");
        } else if (CollectionUtils.isNotEmpty(product.get().getErrors())) {
            upsaleResponse.setError(product.get().getErrors().stream().map(Result.Error::getErrorMsg).collect(Collectors.joining("; ")));
        } else {
            upsaleResponse.setPrice(product.get().getTotalPrice());
            upsaleResponse.setQuoteRequest(quoteRequest);
        }
        fillPolicyCard(policyMeta, packageCode, upsaleResponse, quoteRequest, enabled);
        updateUpsalesConsideringPackages(policyMeta, quoteRequest, null);
        fillTooltips(quoteRequest, policyMeta, upsaleResponse);
        return upsaleResponse;
    }

    public void updateUpsalesConsideringPackages(PolicyMeta policyMeta, QuoteRequest quoteRequest, String changedCategoryCode) {
        List<String> enabledBefore = new ArrayList<>(quoteRequest.getEnabledPackages());
        // fill enabled packages for quoteRequest
        fillPolicyMetaPackages(policyMeta, quoteRequest);
        List<String> disabledPackageCodes = enabledBefore.stream().filter(packageCode -> !quoteRequest.getEnabledPackages().contains(packageCode)).collect(Collectors.toList());

        List<PolicyMetaPackage> pmPackages = policyMetaService.getPackagesFilteredByRestrictions(policyMeta, quoteRequest);

        // restore upsales included in disabled packages to default
        pmPackages.stream().filter(pmPackage -> disabledPackageCodes.contains(pmPackage.getCode())).forEach(pmPackage ->
                pmPackage.getPolicyMetaPackageValues().forEach(pmPackageValue -> {
                            if (StringUtils.isBlank(changedCategoryCode) || !StringUtils.equals(pmPackageValue.getPolicyMetaCategory().getCategory().getCode(), changedCategoryCode)) {
                                PolicyMetaCategoryValue defaultValue = policyMetaCategoryValueService.getFirstAcceptedCategoryValue(pmPackageValue.getPolicyMetaCategory().getValues(), quoteRequest);
                                quoteRequest.getCategories().put(pmPackageValue.getPolicyMetaCategory().getCategory().getCode(), defaultValue.getValue());
                            }
                        }
                ));
    }

    public void enabledUpsalesUsingPackagesFromFilters(PolicyMeta policyMeta, QuoteRequest quoteRequest) {
        List<PolicyMetaPackage> pmPackages = policyMetaService.getPackagesFilteredByRestrictions(policyMeta, quoteRequest);
        // if ANY upsale in package enabled then package enabled
        pmPackages.forEach(pmPackage -> {
            boolean enabled = pmPackage.getPolicyMetaPackageValues().stream().anyMatch(policyMetaPackageValue -> {
                        PolicyMetaCategoryValue value = policyMetaService.findCategoryUpsaleValueForSelectedValue(
                                quoteRequest.getCategories().get(policyMetaPackageValue.getPolicyMetaCategory().getCategory().getCode()), policyMetaPackageValue.getPolicyMetaCategory(), quoteRequest);
                        return StringUtils.equals(value != null ? value.getValue() : StringUtils.EMPTY, policyMetaPackageValue.getValue());
                    }
            );
            if (enabled) {
                pmPackage.getPolicyMetaPackageValues().forEach(pmPackageValue -> quoteRequest.getCategories().put(pmPackageValue.getPolicyMetaCategory().getCategory().getCode(), pmPackageValue.getValue()));
            }
        });
        fillPolicyMetaPackages(policyMeta, quoteRequest);
    }

    private void fillPolicyMetaPackages(PolicyMeta policyMeta, QuoteRequest quoteRequest) {
        quoteRequest.getEnabledPackages().clear();
        List<PolicyMetaPackage> pmPackages = policyMetaService.getPackagesFilteredByRestrictions(policyMeta, quoteRequest);
        // if ALL upsale in package enabled then package enabled
        pmPackages.forEach(pmPackage -> {
            boolean enabled = pmPackage.getPolicyMetaPackageValues().stream().allMatch(policyMetaPackageValue ->
                    StringUtils.equals(quoteRequest.getCategories().get(policyMetaPackageValue.getPolicyMetaCategory().getCategory().getCode()), policyMetaPackageValue.getValue())
            );
            if (enabled) {
                quoteRequest.getEnabledPackages().add(pmPackage.getCode());
            }
        });
    }

    private void fillPolicyCard(PolicyMeta policyMeta, String packageCode, UpsaleResponseDTO upsaleResponse, QuoteRequest quoteRequest, boolean enabled) {
        PolicyMetaPackage pmPackage = policyMetaService.getPolicyMetaPackageByCode(packageCode, policyMeta);
        pmPackage.getPolicyMetaPackageValues().forEach(policyMetaPackageValue -> {
                    String value = enabled ? policyMetaPackageValue.getValue() : policyMetaService.getDefaultValueForCategory(policyMetaPackageValue.getPolicyMetaCategory(), quoteRequest).getValue();
                    setPlanDescriptionCategoriesCaption(policyMetaPackageValue.getPolicyMetaCategory().getCategory().getCode(), policyMetaPackageValue.getPolicyMetaCategory().getValues(), value, upsaleResponse);
                }
        );
    }

    public void fillPackageChangeNotifications(QuoteRequest oldQuoteRequest, QuoteRequest newQuoteRequest, PolicyMeta policyMeta, UpsaleResponseDTO upsaleResponseDTO) {
        List<PolicyMetaPackage> pmPackages = policyMetaService.getPackagesFilteredByRestrictions(policyMeta, newQuoteRequest);
        // find disabled packages
        oldQuoteRequest.getEnabledPackages().stream().filter(packageCode -> !newQuoteRequest.getEnabledPackages().contains(packageCode)).forEach(packageCode -> {
            upsaleResponseDTO.getNotifications().add(new NotificationDTO(pmPackages.stream()
                    .filter(pmPackage -> StringUtils.equals(pmPackage.getCode(), packageCode)).findFirst().orElseThrow(() -> new RuntimeException("package not found")).getName(), NotificationColor.RED));
        });

        newQuoteRequest.getEnabledPackages().stream().filter(packageCode -> !oldQuoteRequest.getEnabledPackages().contains(packageCode)).forEach(packageCode -> {
            upsaleResponseDTO.getNotifications().add(new NotificationDTO(pmPackages.stream()
                    .filter(pmPackage -> StringUtils.equals(pmPackage.getCode(), packageCode)).findFirst().orElseThrow(() -> new RuntimeException("package not found")).getName(), NotificationColor.GREEN));
        });
    }

    public void fillTooltips(QuoteRequest quoteRequest, PolicyMeta policyMeta, UpsaleResponseDTO upsaleResponseDTO) {
        List<PolicyMetaCategory> policyMetaCategories = policyMetaService.getUpsalesFromPolicy(policyMeta, quoteRequest);
        List<PolicyMetaPackage> pmPackages = policyMetaService.getPackagesFilteredByRestrictions(policyMeta, quoteRequest);
        policyMetaCategories.forEach(policyMetaCategory -> {
            List<PolicyMetaPackage> packagesWithCategory = pmPackages.stream().filter(policyMetaPackage -> isPackageContainsCategory(policyMetaPackage, policyMetaCategory)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(packagesWithCategory)) {
                Map<String, TooltipMessageDTO> tooltips = new HashMap<>();
                String currentValue = StringUtils.defaultIfBlank(quoteRequest.getCategoryValue(policyMetaCategory.getCategory().getCode()), "0");
                packagesWithCategory.forEach(policyMetaPackage -> {
                    String packageValue = findPolicyMetaPackageValueByCategory(policyMetaPackage, policyMetaCategory).getValue();
                    boolean isValueEnabled = StringUtils.equals(packageValue, currentValue);
                    if (isValueEnabled) {
                        policyMetaCategory.getValues().forEach(policyMetaCategoryValue -> {
                            if (!StringUtils.equals(policyMetaCategoryValue.getValue(), currentValue)) {
                                TooltipMessageDTO tooltip = null;
                                if (tooltips.containsKey(policyMetaCategoryValue.getValue())) {
                                    tooltip = tooltips.get(policyMetaCategoryValue.getValue());
                                    tooltip.addMessage("Changing this category value will turn off originally selected package <b>\"" + policyMetaPackage.getName() + "\"</b>");
                                } else {
                                    tooltip = new TooltipMessageDTO("Changing this category value will turn off originally selected package <b>\"" + policyMetaPackage.getName() + "\"</b>",
                                            policyMetaCategory.getCategory().getId().toString(), policyMetaCategoryValue.getValue());
                                }
                                tooltips.put(policyMetaCategoryValue.getValue(), tooltip);
                            }
                        });
                    } else {
                        TooltipMessageDTO tooltip = null;
                        if (tooltips.containsKey(packageValue)) {
                            tooltip = tooltips.get(packageValue);
                            tooltip.addMessage("Selecting this category value will result in selecting the package <b>\"" + policyMetaPackage.getName() + "\"</b>");
                        } else {
                            tooltip = new TooltipMessageDTO("Selecting this category value will result in selecting the package <b>\"" + policyMetaPackage.getName() + "\"</b>",
                                    policyMetaCategory.getCategory().getId().toString(), packageValue);
                        }
                        tooltips.put(packageValue, tooltip);
                    }
                });
                upsaleResponseDTO.getTooltips().addAll(tooltips.values());
            }
        });
    }


    private boolean isPackageContainsCategory(PolicyMetaPackage policyMetaPackage, PolicyMetaCategory policyMetaCategory) {
        return policyMetaPackage.getPolicyMetaPackageValues().stream().filter(policyMetaPackageValue -> Objects.equals(policyMetaPackageValue.getPolicyMetaCategory().getCategory(), policyMetaCategory.getCategory())).count() > 0;
    }

    private PolicyMetaPackageValue findPolicyMetaPackageValueByCategory(PolicyMetaPackage policyMetaPackage, PolicyMetaCategory policyMetaCategory) {
        return policyMetaPackage.getPolicyMetaPackageValues().stream()
                .filter(policyMetaPackageValue -> Objects.equals(policyMetaPackageValue.getPolicyMetaCategory().getCategory(), policyMetaCategory.getCategory()))
                .findFirst().orElseThrow(() -> new RuntimeException("Package value not found"));
    }

    public void setPlanDescriptionCategoriesCaption(String paramKey, List<PolicyMetaCategoryValue> categoryValues, String val, UpsaleResponseDTO jsonDetailsResult) {
        SystemSettings systemSettings = systemSettingsService.getDefault();
        if (systemSettings.getPlanDescriptionCategory1().getCode().equals(paramKey)) {
            for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
                if (policyMetaCategoryValue.getValue().equals(val)) {
                    jsonDetailsResult.setPlanDescriptionCategoryCaption1(policyMetaCategoryValue.getCaption());
                    break;
                }
            }
        }
        if (systemSettings.getPlanDescriptionCategory2().getCode().equals(paramKey)) {
            for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
                if (policyMetaCategoryValue.getValue().equals(val)) {
                    jsonDetailsResult.setPlanDescriptionCategoryCaption2(policyMetaCategoryValue.getCaption());
                    break;
                }
            }
        }
        if (systemSettings.getPlanDescriptionCategory3().getCode().equals(paramKey)) {
            for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
                if (policyMetaCategoryValue.getValue().equalsIgnoreCase(val)) {
                    jsonDetailsResult.setPlanDescriptionCategoryCaption3(policyMetaCategoryValue.getCaption());
                    break;
                }
            }
        }
        if (systemSettings.getPlanDescriptionCategory4().getCode().equals(paramKey)) {
            for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
                if (policyMetaCategoryValue.getValue().equals(val)) {
                    jsonDetailsResult.setPlanDescriptionCategoryCaption4(policyMetaCategoryValue.getCaption());
                    break;
                }
            }
        }
        if (systemSettings.getPlanDescriptionCategory5().getCode().equals(paramKey)) {
            for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
                if (policyMetaCategoryValue.getValue().equals(val)) {
                    jsonDetailsResult.setPlanDescriptionCategoryCaption5(policyMetaCategoryValue.getCaption());
                    break;
                }
            }
        }
        if (systemSettings.getPlanDescriptionCategory6().getCode().equals(paramKey)) {
            for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
                if (policyMetaCategoryValue.getValue().equals(val)) {
                    jsonDetailsResult.setPlanDescriptionCategoryCaption6(policyMetaCategoryValue.getCaption());
                    break;
                }
            }
        }
    }

}
