package com.travelinsurancemaster.util;

import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.dto.PolicyMetaCategory;
import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import com.travelinsurancemaster.model.dto.ValueType;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.PolicyMetaService;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

/**
 * Created by ritchie on 8/1/16.
 */
public class ConditionalCategoryUtil {

    private QuoteRequest quoteRequest;
    private List<PolicyMetaCategory> policyMetaCategoryList = new ArrayList<>();
    private Map<String, String> upsaleValueMap = new HashMap<>();
    private PolicyMetaService policyMetaService;

    public ConditionalCategoryUtil() {
    }

    public ConditionalCategoryUtil(QuoteRequest quoteRequest) {
        this.quoteRequest = quoteRequest;
    }

    public ConditionalCategoryUtil(QuoteRequest quoteRequest, List<PolicyMetaCategory> policyMetaCategoryList, PolicyMetaService policyMetaService) {
        this.quoteRequest = quoteRequest;
        this.policyMetaCategoryList = policyMetaCategoryList;
        this.policyMetaService = policyMetaService;
    }

    public QuoteRequest getQuoteRequest() {
        return quoteRequest;
    }

    public void setQuoteRequest(QuoteRequest quoteRequest) {
        this.quoteRequest = quoteRequest;
    }

    public List<PolicyMetaCategory> getPolicyMetaCategoryList() {
        return policyMetaCategoryList;
    }

    public void setPolicyMetaCategoryList(List<PolicyMetaCategory> policyMetaCategoryList) {
        this.policyMetaCategoryList = policyMetaCategoryList;
    }

    public Map<String, String> getUpsaleValueMap() {
        return upsaleValueMap;
    }

    public void setUpsaleValueMap(Map<String, String> upsaleValueMap) {
        this.upsaleValueMap = upsaleValueMap;
    }

    public boolean has(String code) {
        return has(code, null, null);
    }

    /**
     * @param code - category code
     * @param type - valueType to which val must be converted
     * @param val - value for filtering
     * @return true if policyMetaCategory matches the condition, otherwise - false.
     */
    public boolean has(String code, String type, String val) {
        Optional<PolicyMetaCategory> policyMetaCategoryMatched = policyMetaCategoryList.stream()
                .filter(policyMetaCategory -> code.equals(policyMetaCategory.getCategory().getCode()))
                .findFirst();
        if (!policyMetaCategoryMatched.isPresent()) {
            return false;
        }
        if (type != null && val != null) {
            PolicyMetaCategory policyMetaCategory = policyMetaCategoryMatched.get();
            PolicyMetaCategoryValue policyMetaCategoryValue;
            if (policyMetaCategory.getType() == PolicyMetaCategory.MetaParamType.UP_SALE) {
                policyMetaCategoryValue = policyMetaService.findCategoryUpsaleValueForSelectedValue(val, policyMetaCategory, quoteRequest);
            } else {
                policyMetaCategoryValue = policyMetaCategory.getValues().get(0);
            }
            ValueType valueType;
            if (type.equals('$')) {
                valueType = ValueType.FIX;
            } else if (type.equals('%')) {
                valueType = ValueType.PERCENT;
            } else {
                valueType = ValueType.NAN;
            }
            Integer categoryVal = policyMetaCategoryValue.getValueByType(valueType, quoteRequest.getTripCostPerTraveler());
            Integer intVal = NumberUtils.toInt(val);
            boolean found = false;
            if (code.equals(CategoryCodes.LOOK_BACK_PERIOD)) {
                if (categoryVal != null && intVal >= categoryVal) {
                    found = true;
                }
            } else {
                if (categoryVal != null && intVal <= categoryVal) {
                    found = true;
                }
            }
            if (found) {
                upsaleValueMap.put(code, policyMetaCategoryValue.getValue());
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
}
