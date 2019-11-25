package com.travelinsurancemaster.model.dto.validator;

import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.PercentInfo;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.CommissionService;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Chernov Artur on 25.08.15.
 */
@Component
public interface PercentValidator {

    CommissionService getCommissionService();

    default void validatePercents(Errors errors, List<PercentInfo> percentInfoList, PercentType percentType, String field) {
        String typeString = ((percentType.getId() == PercentType.PERCENT.getId()) || (percentType.getId() == PercentType.RANGE_PERCENT.getId())) ? "Percent" : "Fixed";
        if (percentType.getId() != PercentType.NONE.getId()) {
            if (CollectionUtils.isEmpty(percentInfoList)) {
                errors.rejectValue(field, null, typeString + " values is required");
            }
        }
        for (PercentInfo percentInfo : percentInfoList) {
            if (percentType.getId() != PercentType.NONE.getId() && percentType.getId() != PercentType.CALCULATED.getId()) {
                if (percentInfo.getValue() == null) {
                    errors.reject(field + ".value", typeString + " value is required");
                }
            }
            if (percentType.getId() == PercentType.RANGE_FIXED.getId() || percentType.getId() == PercentType.RANGE_PERCENT.getId()) {
                if (percentInfo.getValueFrom() == null) {
                    errors.reject(field + ".valueFrom", typeString + " value from is required");
                }
                if (percentInfo.getValueTo() == null) {
                    errors.reject(field + ".valueTo", typeString + " value to is required");

                }
                if (percentInfo.getValueFrom() != null && percentInfo.getValueTo() != null && percentInfo.getValueFrom() > percentInfo.getValueTo()) {
                    errors.reject(field + ".valueTo", "'Value To' must be greater");
                }
            }
            if (percentType.getId() == PercentType.PERCENT.getId() || percentType.getId() == PercentType.RANGE_PERCENT.getId()) {
                if (percentInfo.getValue() != null && (percentInfo.getValue() < 0 || percentInfo.getValue() > 100)) {
                    errors.reject(field + ".value", "Interval must be a valid percentage between 0 and 100");
                }
            }
            if (percentType.getId() == PercentType.CALCULATED.getId()) {
                if (percentInfo.getTextValue() == null) {
                    errors.rejectValue(field + "[0].textValue", null, "Percent text value is required");
                } else try {
                    QuoteRequest quoteRequest = ValidationUtils.getSimpleQuoteRequest();
                    Object result = getCommissionService().invokeGetCommissionFunction(percentInfo.getTextValue(), (double) 100,
                            quoteRequest.getDestinationCountry().name(), quoteRequest.getTripCost().doubleValue(), DateUtil.getLocalDateStr(quoteRequest.getDepartDate()),
                            DateUtil.getLocalDateStr(quoteRequest.getReturnDate()), quoteRequest.getResidentState().name(),
                            quoteRequest.getResidentCountry().name(), quoteRequest.getCitizenCountry().name(), quoteRequest.getTravelers().stream().map(GenericTraveler::getAge).collect(Collectors.toList()));
                    BigDecimal resultVal = new BigDecimal((Double) result);
                } catch (Exception e) {
                    errors.rejectValue(field + "[0].textValue", null, "Illegal script expression");
                }
            }
        }
    }
}
