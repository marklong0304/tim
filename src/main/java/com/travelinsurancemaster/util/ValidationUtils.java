package com.travelinsurancemaster.util;

import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Chernov Artur on 23.06.15.
 */
public final class ValidationUtils {
    private static final String EMPTY_JSON_ARRAY = "[]";

    // If paramValue eq field
    public static void rejectIfEmptyParam(Map<String, String> params, Errors errors, String field, String defaultErrorMsg) {
        rejectIfEmptyParam(params, errors, field, field, defaultErrorMsg);
    }

    public static void rejectIfEmptyParam(Map<String, String> params, Errors errors, String paramValue, String field, String defaultErrorMsg) {
        if (StringUtils.isEmpty(params.get(paramValue))) {
            errors.rejectValue(field, null, defaultErrorMsg);
        }
    }

    public static void rejectIfEmptyJsonArrayParam(Map<String, String> params, Errors errors, String field, String defaultErrorMsg) {
        rejectIfEmptyJsonArrayParam(params, errors, field, field, defaultErrorMsg);
    }

    public static void rejectIfEmptyJsonArrayParam(Map<String, String> params, Errors errors, String param, String field, String defaultErrorMsg) {
        if (StringUtils.equals(EMPTY_JSON_ARRAY, params.get(param))) {
            errors.rejectValue(field, null, defaultErrorMsg);
        }
    }

    public static void validatePhone(Errors errors, String phone, String field) {
        validatePhone(errors, phone, field, null);
    }

    public static void validatePhone(Errors errors, String phone, String field, String vendorCode) {
        if (!phone.matches("^\\+?[\\d\\(\\) \\-\\.]{5,30}$")) {
            errors.rejectValue(field, null, "Phone has incorrect format!");
        } else if ((Objects.equals(vendorCode, ApiVendor.TravelSafe) ||
                Objects.equals(vendorCode, ApiVendor.MHRoss)) &&
                phone.replaceAll("[^0-9]", "").length() > 10) {
            errors.rejectValue(field, null, "Phone length exceeds 10 digits!");
        }
    }

    public static boolean isValidZipCode(String zipCode, String countryStatePair) {
        if (org.springframework.util.StringUtils.isEmpty(countryStatePair) && countryStatePair.length() < 2) {
            return false;
        }
        String countryCode = countryStatePair.substring(0, 2);
        switch (countryCode) {
            case "GB":
                return zipCode.matches("^GIR[ ]?0AA|((AB|AL|B|BA|BB|BD|BH|BL|BN|BR|BS|BT|CA|CB|CF|CH|CM|CO|CR|CT|CV|CW|DA|DD|DE|DG|DH|DL|DN|DT|DY|E|EC|EH|EN|EX|FK|FY|G|GL|GY|GU|HA|HD|HG|HP|HR|HS|HU|HX|IG|IM|IP|IV|JE|KA|KT|KW|KY|L|LA|LD|LE|LL|LN|LS|LU|M|ME|MK|ML|N|NE|NG|NN|NP|NR|NW|OL|OX|PA|PE|PH|PL|PO|PR|RG|RH|RM|S|SA|SE|SG|SK|SL|SM|SN|SO|SP|SR|SS|ST|SW|SY|TA|TD|TF|TN|TQ|TR|TS|TW|UB|W|WA|WC|WD|WF|WN|WR|WS|WV|YO|ZE)(\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}))|BFPO[ ]?\\d{1,4}$");
            case "JE":
                return zipCode.matches("^JE\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}$");
            case "GG":
                return zipCode.matches("^GY\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}$");
            case "IM":
                return zipCode.matches("^IM\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}$");
            case "US":
                return zipCode.matches("^\\d{5}([ \\-]\\d{4})?$");
            case "CA":
                return zipCode.matches("^[ABCEGHJKLMNPRSTVXY]\\d[ABCEGHJ-NPRSTV-Z][ ]?\\d[ABCEGHJ-NPRSTV-Z]\\d$");
            case "DE":
                return zipCode.matches("^\\d{5}$");
            case "JP":
                return zipCode.matches("^\\d{3}-\\d{4}$");
            case "FR":
                return zipCode.matches("^\\d{2}[ ]?\\d{3}$");
            case "AU":
                return zipCode.matches("^\\d{4}$");
            case "IT":
                return zipCode.matches("^\\d{5}$");
            case "CH":
                return zipCode.matches("^\\d{4}$");
            case "AT":
                return zipCode.matches("^\\d{4}$");
            case "ES":
                return zipCode.matches("^\\d{5}$");
            case "NL":
                return zipCode.matches("^\\d{4}[ ]?[A-Z]{2}$");
            case "BE":
                return zipCode.matches("^\\d{4}$");
            case "DK":
                return zipCode.matches("^\\d{4}$");
            case "SE":
                return zipCode.matches("^\\d{3}[ ]?\\d{2}$");
            case "NO":
                return zipCode.matches("^\\d{4}$");
            case "BR":
                return zipCode.matches("^\\d{5}[\\-]?\\d{3}$");
            case "PT":
                return zipCode.matches("^\\d{4}([\\-]\\d{3})?$");
            case "FI":
                return zipCode.matches("^\\d{5}$");
            case "AX":
                return zipCode.matches("^22\\d{3}$");
            case "KR":
                return zipCode.matches("^\\d{3}[\\-]\\d{3}$");
            case "CN":
                return zipCode.matches("^\\d{6}$");
            case "TW":
                return zipCode.matches("^\\d{3}(\\d{2})?$");
            case "SG":
                return zipCode.matches("^\\d{6}$");
            case "DZ":
                return zipCode.matches("^\\d{5}$");
            case "AD":
                return zipCode.matches("^AD\\d{3}$");
            case "AR":
                return zipCode.matches("^([A-HJ-NP-Z])?\\d{4}([A-Z]{3})?$");
            case "AM":
                return zipCode.matches("^(37)?\\d{4}$");
            case "AZ":
                return zipCode.matches("^\\d{4}$");
            case "BH":
                return zipCode.matches("^((1[0-2]|[2-9])\\d{2})?$");
            case "BD":
                return zipCode.matches("^\\d{4}$");
            case "BB":
                return zipCode.matches("^(BB\\d{5})?$");
            case "BY":
                return zipCode.matches("^\\d{6}$");
            case "BM":
                return zipCode.matches("^[A-Z]{2}[ ]?[A-Z0-9]{2}$");
            case "BA":
                return zipCode.matches("^\\d{5}$");
            case "IO":
                return zipCode.matches("^BBND 1ZZ$");
            case "BN":
                return zipCode.matches("^[A-Z]{2}[ ]?\\d{4}$");
            case "BG":
                return zipCode.matches("^\\d{4}$");
            case "KH":
                return zipCode.matches("^\\d{5}$");
            case "CV":
                return zipCode.matches("^\\d{4}$");
            case "CL":
                return zipCode.matches("^\\d{7}$");
            case "CR":
                return zipCode.matches("^\\d{4,5}|\\d{3}-\\d{4}$");
            case "HR":
                return zipCode.matches("^\\d{5}$");
            case "CY":
                return zipCode.matches("^\\d{4}$");
            case "CZ":
                return zipCode.matches("^\\d{3}[ ]?\\d{2}$");
            case "DO":
                return zipCode.matches("^\\d{5}$");
            case "EC":
                return zipCode.matches("^([A-Z]\\d{4}[A-Z]|(?:[A-Z]{2})?\\d{6})?$");
            case "EG":
                return zipCode.matches("^\\d{5}$");
            case "EE":
                return zipCode.matches("^\\d{5}$");
            case "FO":
                return zipCode.matches("^\\d{3}$");
            case "GE":
                return zipCode.matches("^\\d{4}$");
            case "GR":
                return zipCode.matches("^\\d{3}[ ]?\\d{2}$");
            case "GL":
                return zipCode.matches("^39\\d{2}$");
            case "GT":
                return zipCode.matches("^\\d{5}$");
            case "HT":
                return zipCode.matches("^\\d{4}$");
            case "HN":
                return zipCode.matches("^(?:\\d{5})?$");
            case "HU":
                return zipCode.matches("^\\d{4}$");
            case "IS":
                return zipCode.matches("^\\d{3}$");
            case "IN":
                return zipCode.matches("^\\d{6}$");
            case "ID":
                return zipCode.matches("^\\d{5}$");
            case "IL":
                return zipCode.matches("^\\d{5}$");
            case "JO":
                return zipCode.matches("^\\d{5}$");
            case "KZ":
                return zipCode.matches("^\\d{6}$");
            case "KE":
                return zipCode.matches("^\\d{5}$");
            case "KW":
                return zipCode.matches("^\\d{5}$");
            case "LA":
                return zipCode.matches("^\\d{5}$");
            case "LV":
                return zipCode.matches("^\\d{4}$");
            case "LB":
                return zipCode.matches("^(\\d{4}([ ]?\\d{4})?)?$");
            case "LI":
                return zipCode.matches("^(948[5-9])|(949[0-7])$");
            case "LT":
                return zipCode.matches("^\\d{5}$");
            case "LU":
                return zipCode.matches("^\\d{4}$");
            case "MK":
                return zipCode.matches("^\\d{4}$");
            case "MY":
                return zipCode.matches("^\\d{5}$");
            case "MV":
                return zipCode.matches("^\\d{5}$");
            case "MT":
                return zipCode.matches("^[A-Z]{3}[ ]?\\d{2,4}$");
            case "MU":
                return zipCode.matches("^(\\d{3}[A-Z]{2}\\d{3})?$");
            case "MX":
                return zipCode.matches("^\\d{5}$");
            case "MD":
                return zipCode.matches("^\\d{4}$");
            case "MC":
                return zipCode.matches("^980\\d{2}$");
            case "MA":
                return zipCode.matches("^\\d{5}$");
            case "NP":
                return zipCode.matches("^\\d{5}$");
            case "NZ":
                return zipCode.matches("^\\d{4}$");
            case "NI":
                return zipCode.matches("^((\\d{4}-)?\\d{3}-\\d{3}(-\\d{1})?)?$");
            case "NG":
                return zipCode.matches("^(\\d{6})?$");
            case "OM":
                return zipCode.matches("^(PC )?\\d{3}$");
            case "PK":
                return zipCode.matches("^\\d{5}$");
            case "PY":
                return zipCode.matches("^\\d{4}$");
            case "PH":
                return zipCode.matches("^\\d{4}$");
            case "PL":
                return zipCode.matches("^\\d{2}-\\d{3}$");
            case "PR":
                return zipCode.matches("^00[679]\\d{2}([ \\-]\\d{4})?$");
            case "RO":
                return zipCode.matches("^\\d{6}$");
            case "RU":
                return zipCode.matches("^\\d{6}$");
            case "SM":
                return zipCode.matches("^4789\\d$");
            case "SA":
                return zipCode.matches("^\\d{5}$");
            case "SN":
                return zipCode.matches("^\\d{5}$");
            case "SK":
                return zipCode.matches("^\\d{3}[ ]?\\d{2}$");
            case "SI":
                return zipCode.matches("^\\d{4}$");
            case "ZA":
                return zipCode.matches("^\\d{4}$");
            case "LK":
                return zipCode.matches("^\\d{5}$");
            case "TJ":
                return zipCode.matches("^\\d{6}$");
            case "TH":
                return zipCode.matches("^\\d{5}$");
            case "TN":
                return zipCode.matches("^\\d{4}$");
            case "TR":
                return zipCode.matches("^\\d{5}$");
            case "TM":
                return zipCode.matches("^\\d{6}$");
            case "UA":
                return zipCode.matches("^\\d{5}$");
            case "UY":
                return zipCode.matches("^\\d{5}$");
            case "UZ":
                return zipCode.matches("^\\d{6}$");
            case "VA":
                return zipCode.matches("^00120$");
            case "VE":
                return zipCode.matches("^\\d{4}$");
            case "ZM":
                return zipCode.matches("^\\d{5}$");
            case "AS":
                return zipCode.matches("^96799$");
            case "CC":
                return zipCode.matches("^6799$");
            case "CK":
                return zipCode.matches("^\\d{4}$");
            case "RS":
                return zipCode.matches("^\\d{6}$");
            case "ME":
                return zipCode.matches("^8\\d{4}$");
            case "CS":
                return zipCode.matches("^\\d{5}$");
            case "YU":
                return zipCode.matches("^\\d{5}$");
            case "CX":
                return zipCode.matches("^6798$");
            case "ET":
                return zipCode.matches("^\\d{4}$");
            case "FK":
                return zipCode.matches("^FIQQ 1ZZ$");
            case "NF":
                return zipCode.matches("^2899$");
            case "FM":
                return zipCode.matches("^(9694[1-4])([ \\-]\\d{4})?$");
            case "GF":
                return zipCode.matches("^9[78]3\\d{2}$");
            case "GN":
                return zipCode.matches("^\\d{3}$");
            case "GP":
                return zipCode.matches("^9[78][01]\\d{2}$");
            case "GS":
                return zipCode.matches("^SIQQ 1ZZ$");
            case "GU":
                return zipCode.matches("^969[123]\\d([ \\-]\\d{4})?$");
            case "GW":
                return zipCode.matches("^\\d{4}$");
            case "HM":
                return zipCode.matches("^\\d{4}$");
            case "IQ":
                return zipCode.matches("^\\d{5}$");
            case "KG":
                return zipCode.matches("^\\d{6}$");
            case "LR":
                return zipCode.matches("^\\d{4}$");
            case "LS":
                return zipCode.matches("^\\d{3}$");
            case "MG":
                return zipCode.matches("^\\d{3}$");
            case "MH":
                return zipCode.matches("^969[67]\\d([ \\-]\\d{4})?$");
            case "MN":
                return zipCode.matches("^\\d{6}$");
            case "MP":
                return zipCode.matches("^9695[012]([ \\-]\\d{4})?$");
            case "MQ":
                return zipCode.matches("^9[78]2\\d{2}$");
            case "NC":
                return zipCode.matches("^988\\d{2}$");
            case "NE":
                return zipCode.matches("^\\d{4}$");
            case "VI":
                return zipCode.matches("^008(([0-4]\\d)|(5[01]))([ \\-]\\d{4})?$");
            case "PF":
                return zipCode.matches("^987\\d{2}$");
            case "PG":
                return zipCode.matches("^\\d{3}$");
            case "PM":
                return zipCode.matches("^9[78]5\\d{2}$");
            case "PN":
                return zipCode.matches("^PCRN 1ZZ$");
            case "PW":
                return zipCode.matches("^96940$");
            case "RE":
                return zipCode.matches("^9[78]4\\d{2}$");
            case "SH":
                return zipCode.matches("^(ASCN|STHL) 1ZZ$");
            case "SJ":
                return zipCode.matches("^\\d{4}$");
            case "SO":
                return zipCode.matches("^\\d{5}$");
            case "SZ":
                return zipCode.matches("^[HLMS]\\d{3}$");
            case "TC":
                return zipCode.matches("^TKCA 1ZZ$");
            case "WF":
                return zipCode.matches("^986\\d{2}$");
            case "XK":
                return zipCode.matches("^\\d{5}$");
            case "YT":
                return zipCode.matches("^976\\d{2}$");
            default:
                return zipCode.matches("^(?:[A-Z0-9]+([- ]?[A-Z0-9]+)*)?$");
        }
    }

    /**
     * @return populated quote request for validation purpose
     */
    public static QuoteRequest getSimpleQuoteRequest() {
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setDestinationCountry(CountryCode.ES);
        LocalDate today = LocalDate.now();
        quoteRequest.setDepartDate(today.plusWeeks(1));
        quoteRequest.setReturnDate(today.plusWeeks(3));
        quoteRequest.setPaymentDate(today.minusWeeks(1));
        quoteRequest.setDepositDate(today.minusWeeks(3));
        quoteRequest.setTripCost(new BigDecimal("1000.0"));
        quoteRequest.setTripCostTotal(true);
        GenericTraveler traveler = new GenericTraveler(25);
        quoteRequest.getTravelers().add(traveler);
        quoteRequest.setResidentCountry(CountryCode.US);
        quoteRequest.setResidentState(StateCode.CA);
        quoteRequest.setCitizenCountry(CountryCode.US);
        return quoteRequest;
    }
}
