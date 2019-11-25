package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Chernov Artur on 09.10.15.
 */

@Service
@Transactional
public class PolicyMetaCategoryValueService {

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    @Autowired
    private PolicyMetaService policyMetaService;



    @Transactional(readOnly = true)
    public List<PolicyMetaCategoryValue> getSortedCategoryValues(Long policyMetaId, String categoryCode, QuoteRequest quoteRequest) {
        List<PolicyMetaCategoryValue> categoryValues = policyMetaCategoryService.getCategoryValues(policyMetaId, categoryCode, quoteRequest);
        if (categoryValues == null) {
            return Collections.emptyList();
        }
        Collections.sort(categoryValues);
        return categoryValues;
    }

    /**
     * @return true if value is default (empty or first), otherwise - false.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isDefaultValue(List<PolicyMetaCategoryValue> categoryValuesList, String value) {
        if (CollectionUtils.isEmpty(categoryValuesList) || StringUtils.isEmpty(value)) {
            return true;
        }
        return value.equals(categoryValuesList.get(0).getValue());
    }

    /**
     * @return policyMetaCategoryValue matched the conditions
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public PolicyMetaCategoryValue getFirstAcceptedCategoryValue(List<PolicyMetaCategoryValue> categoryValues, QuoteRequest quoteRequest) {
        if (CollectionUtils.isEmpty(categoryValues)) {
            return null;
        }
        for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
            if (policyMetaService.checkPolicyMetaCategoryValueByConditions(policyMetaCategoryValue, quoteRequest, false)) {
                return policyMetaCategoryValue;
            }
        }
        return null;
    }

    /**
     * @return List<PolicyMetaCategoryValue> matched the conditions
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<PolicyMetaCategoryValue> getAllAcceptedCategoryValue(List<PolicyMetaCategoryValue> categoryValues, QuoteRequest quoteRequest) {
        if (CollectionUtils.isEmpty(categoryValues)) {
            return Collections.emptyList();
        }
        List<PolicyMetaCategoryValue> policyMetaCategoryValues = new ArrayList<>();
        for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
            if (policyMetaService.checkPolicyMetaCategoryValueByConditions(policyMetaCategoryValue, quoteRequest, false)) {
                policyMetaCategoryValues.add(policyMetaCategoryValue);
            }
        }
        return policyMetaCategoryValues;
    }

    /**
     * @return api value to send to vendors. If api value is not exists - upsale value is selected.
     */
    @Transactional(readOnly = true)
    public String getApiValue(Long policyMetaId, String categoryCode, String upsaleValue, QuoteRequest quoteRequest) {
        List<PolicyMetaCategoryValue> categoryValues = getSortedCategoryValues(policyMetaId, categoryCode, quoteRequest);
        if (upsaleValue == null) {
            PolicyMetaCategoryValue firstCategoryValue = getFirstAcceptedCategoryValue(categoryValues, quoteRequest);
            if (firstCategoryValue == null) {
                return null;
            }
            return firstCategoryValue.getApiValue() != null ? firstCategoryValue.getApiValue() : firstCategoryValue.getValue();
        }
        for (PolicyMetaCategoryValue policyMetaCategoryValue : categoryValues) {
            if (policyMetaCategoryValue.getValue().equalsIgnoreCase(upsaleValue)) {
                // upsale value can be used as api value
                return policyMetaCategoryValue.getApiValue() != null ? policyMetaCategoryValue.getApiValue() : policyMetaCategoryValue.getValue();
            }
        }
        return upsaleValue;
    }
}
