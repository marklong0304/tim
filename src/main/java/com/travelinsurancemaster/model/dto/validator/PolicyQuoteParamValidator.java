package com.travelinsurancemaster.model.dto.validator;

import com.travelinsurancemaster.model.dto.PolicyQuoteParam;
import com.travelinsurancemaster.model.dto.PolicyQuoteParamValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by N.Kurennoy on 22.05.2016.
 */
@Component
public class PolicyQuoteParamValidator implements Validator {
    private static final Logger log = LoggerFactory.getLogger(CategoryValidator.class);

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(PolicyQuoteParamValidator.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("Validating {}", target);
        PolicyQuoteParam quoteParam = (PolicyQuoteParam) target;

        if (quoteParam.getValue() == null || quoteParam.getValue() <= 0) {
            errors.rejectValue("value", null, "Policy Quote Param value must be filled and greater 0");
        }

        if (quoteParam.getListParams().stream().filter(param -> param.getType() != PolicyQuoteParamValue.QuoteParamType.NONE).count() == 0) {
            errors.rejectValue(null, null, "At least one parameter must be filled ");
        }

        int i = 0;
        for (PolicyQuoteParamValue policyQuoteParamValue : quoteParam.getListParams()) {
            i++;
            if (policyQuoteParamValue == null
                    || policyQuoteParamValue.getType() == PolicyQuoteParamValue.QuoteParamType.NONE) continue;

            if (policyQuoteParamValue.getValueFrom() == null || policyQuoteParamValue.getValueFrom() < 0) {
                errors.rejectValue("param" + i + ".valueFrom", null, "ValueFrom must be filled and greater or equal than 0");
            }

            if (policyQuoteParamValue.getType() == PolicyQuoteParamValue.QuoteParamType.RANGE) {
                if (policyQuoteParamValue.getValueTo() == null || policyQuoteParamValue.getValueTo() < 0) {
                    errors.rejectValue("param" + i + ".valueTo", null, "ValueTo must be filled and greater or equal than 0");
                }

                if (policyQuoteParamValue.getValueFrom() != null && policyQuoteParamValue.getValueTo() != null
                        && policyQuoteParamValue.getValueFrom() > policyQuoteParamValue.getValueTo()) {
                    errors.rejectValue("param" + i + ".valueTo", null, "ValueTo must be greater or equal than ValueFrom");
                }
            }


        }
    }
}
