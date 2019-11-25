package com.travelinsurancemaster.model.dto.json;

import com.travelinsurancemaster.model.dto.Restriction;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Created by ritchie on 6/7/16.
 */
public class JsonRestrictionsResponse extends JsonResponse {

    private List<? extends Restriction> restrictionsList;

    public JsonRestrictionsResponse(boolean status, List<ObjectError> allErrors, List<? extends Restriction> restrictionsList) {
        super(status, allErrors);
        this.restrictionsList = restrictionsList;
    }

    public List<? extends Restriction> getRestrictionsList() {
        return restrictionsList;
    }

    public void setRestrictionsList(List<? extends Restriction> restrictionsList) {
        this.restrictionsList = restrictionsList;
    }
}
