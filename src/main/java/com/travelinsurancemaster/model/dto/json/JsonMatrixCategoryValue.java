package com.travelinsurancemaster.model.dto.json;

import com.travelinsurancemaster.model.dto.PolicyMetaCategoryValue;
import com.travelinsurancemaster.model.dto.SubcategoryValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ritchie on 7/12/16.
 */
public class JsonMatrixCategoryValue {

    private PolicyMetaCategoryValue policyMetaCategoryValue;

    private Map<String, SubcategoryValue> subcategoryValueMap = new HashMap<>();

    public JsonMatrixCategoryValue() {
    }

    public JsonMatrixCategoryValue(PolicyMetaCategoryValue policyMetaCategoryValue, Map<String, SubcategoryValue> subcategoryValueMap) {
        this.policyMetaCategoryValue = policyMetaCategoryValue;
        this.subcategoryValueMap = subcategoryValueMap;
    }

    public PolicyMetaCategoryValue getPolicyMetaCategoryValue() {
        return policyMetaCategoryValue;
    }

    public void setPolicyMetaCategoryValue(PolicyMetaCategoryValue policyMetaCategoryValue) {
        this.policyMetaCategoryValue = policyMetaCategoryValue;
    }

    public Map<String, SubcategoryValue> getSubcategoryValueMap() {
        return subcategoryValueMap;
    }

    public void setSubcategoryValueMap(Map<String, SubcategoryValue> subcategoryValueMap) {
        this.subcategoryValueMap = subcategoryValueMap;
    }
}
