package com.travelinsurancemaster.model.dto.validator;

import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.PercentInfo;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.CommissionService;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by N.Kurennoy on 18.05.2016.
 */
@Component
public class CompensationValidator {

    @Autowired
    private CommissionService commissionService;

    public void validate(Map<String, String> errors, List<PercentInfo> percentInfoList, PercentType percentType, String field) {
        String typeString = ((percentType.getId() == PercentType.PERCENT.getId()) || (percentType.getId() == PercentType.RANGE_PERCENT.getId())) ? "Percent" : "Fixed";
        if (percentType.getId() != PercentType.NONE.getId()) {
            if (CollectionUtils.isEmpty(percentInfoList)) {
                errors.put(field, typeString + " values is required");
            }
        }
        int i = 0;
        for (PercentInfo percentInfo : percentInfoList) {
            if (percentType.getId() != PercentType.NONE.getId() && percentType.getId() != PercentType.CALCULATED.getId()) {
                if (percentInfo.getValue() == null) {
                    errors.put("value-" + i, typeString + " value is required");
                }
            }
            if (percentType.getId() == PercentType.RANGE_FIXED.getId() || percentType.getId() == PercentType.RANGE_PERCENT.getId()) {
                if (percentInfo.getValueFrom() == null) {
                    errors.put("valueFrom-" + i, typeString + " value from is required");
                }
                if (percentInfo.getValueTo() == null) {
                    errors.put("valueTo-" + i, typeString + " value to is required");

                }
                if (percentInfo.getValueFrom() != null && percentInfo.getValueTo() != null && percentInfo.getValueFrom() > percentInfo.getValueTo()) {
                    errors.put("valueTo-" + i, "'Value To' must be greater 'Value From'");
                }
            }
            if (percentType.getId() == PercentType.PERCENT.getId() || percentType.getId() == PercentType.RANGE_PERCENT.getId()) {
                if (percentInfo.getValue() != null && (percentInfo.getValue() < 0 || percentInfo.getValue() > 100)) {
                    errors.put("value-" + i, "Interval must be a valid percentage between 0 and 100");
                }
            }
            if (percentType.getId() == PercentType.CALCULATED.getId()) {
                if (percentInfo.getTextValue() == null) {
                    errors.put("textValue", "Percent text value is required");
                } else try {
                    QuoteRequest quoteRequest = ValidationUtils.getSimpleQuoteRequest();
                    Object result = this.commissionService.invokeGetCommissionFunction(percentInfo.getTextValue(), (double) 100,
                            quoteRequest.getDestinationCountry().name(), quoteRequest.getTripCost().doubleValue(), DateUtil.getLocalDateStr(quoteRequest.getDepartDate()),
                            DateUtil.getLocalDateStr(quoteRequest.getReturnDate()), quoteRequest.getResidentState().name(),
                            quoteRequest.getResidentCountry().name(), quoteRequest.getCitizenCountry().name(), quoteRequest.getTravelers().stream().map(GenericTraveler::getAge).collect(Collectors.toList()));
                    BigDecimal resultVal = new BigDecimal((Double) result);
                } catch (Exception e) {
                    errors.put("textValue", "Illegal script expression");
                }
            }
            i++;
        }
    }
}
