package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.system.SystemSettings;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.services.system.SystemSettingsService;
import com.travelinsurancemaster.util.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Chernov Artur on 18.08.15.
 */


@Service
public class CommissionService {

    private static final String CALCULATE_COMMISSION_FUNCTION = "var getCommission = function(price, destinationCountry, tripCost, " +
            "departDateParam, returnDateParam, residenceState, residenceCountry, citizenShip, ages) " +
            "{" + "var departDate = new Date(departDateParam); var returnDateParam = new Date(returnDateParam);" +
            "" + System.lineSeparator() + " ${script}" + System.lineSeparator() + "return result;};";

    private static final List<Role> NOT_ELIGIBLE_FOR_COMMISSION_ROLES =
            Stream.of(
                    Role.ROLE_COMPANY_MANAGER, Role.ROLE_CUSTOMER_SERVICE, Role.ROLE_ACCOUNTANT,
                    Role.ROLE_API, Role.ROLE_CONTENT_MANAGER, Role.ROLE_ADMIN
            ).collect(Collectors.toList());

    @Value("${com.travelinsurancemaster.commission.allRolesAllowed:false}")
    private boolean isAllRolesAllowed;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Autowired
    private AffiliateLinkService affiliateLinkService;

    public void fillAffiliateCommission(QuoteStorage quoteStorage, User user, Purchase purchase, Optional<AffiliateLink> affiliateLink) {

        if(user.getCompany() != null) {
            //User belongs to a company, use company's commissions
            Company company = user.getCompany();
            List<PercentInfo> percentInfoList = company.getPercentInfo();
            PercentType percentType = company.getPercentType();
            Commission commission = calculateRefCommission(percentType, percentInfoList, purchase);
            setAffiliateCommissionToPurchase(purchase, user, commission);
        } else if(user.isAffiliate()) {
            //User is an individual affiliate
            UserInfo userInfo = user.getUserInfo();
            List<PercentInfo> percentInfoList = userInfo.getPercentInfo();
            PercentType percentType = userInfo.getPercentType();
            Commission commission = calculateRefCommission(percentType, percentInfoList, purchase);
            setAffiliateCommissionToPurchase(purchase, user, commission);
        } else if(affiliateLink.isPresent() && affiliateLinkService.isAffiliateLinkEligible(user, affiliateLink.get())) {
            //Purchase has an affiliation link
            User affiliateLinkUser = affiliateLink.get().getUser();
            UserInfo affiliateUserInfo = affiliateLinkUser.getUserInfo();
            List<PercentInfo> percentInfoList = affiliateUserInfo.getPercentInfo();
            PercentType percentType = affiliateUserInfo.getPercentType();
            Commission commission = calculateRefCommission(percentType, percentInfoList, purchase);
            setAffiliateCommissionToPurchase(purchase, affiliateLinkUser, commission);
            //User becomes an affiliate after a purchase has been made using her/his affiliate link
            userService.addRole(affiliateLinkUser, Role.ROLE_AFFILIATE);
        } else if(quoteStorage.getAffiliate() != null) {
            //If quote storage has an affiliate user set, compute commission for her/him
            User quoteStorageUser = quoteStorage.getAffiliate();
            UserInfo affiliateUserInfo = quoteStorageUser.getUserInfo();
            List<PercentInfo> percentInfoList = affiliateUserInfo.getPercentInfo();
            PercentType percentType = affiliateUserInfo.getPercentType();
            Commission commission = calculateRefCommission(percentType, percentInfoList, purchase);
            setAffiliateCommissionToPurchase(purchase, quoteStorageUser, commission);
            //User becomes an affiliate after a purchase has been made using her/his affiliate link
            userService.addRole(quoteStorageUser, Role.ROLE_AFFILIATE);
        } else if(user.getUserInfo().isCompany() && user.getUserInfo().getPercentType().getId() != PercentType.NONE.getId()) {
            //Old style company user, consider to remove after analysis
            UserInfo userInfo = user.getUserInfo();
            List<PercentInfo> percentInfoList = userInfo.getPercentInfo();
            PercentType percentType = userInfo.getPercentType();
            Commission commission = calculateRefCommission(percentType, percentInfoList, purchase);
            setAffiliateCommissionToPurchase(purchase, user, commission);
        }
    }

    private void setAffiliateCommissionToPurchase(Purchase purchase, User affiliateUser, Commission commission) {
        if(isAllRolesAllowed || CollectionUtils.intersection(affiliateUser.getRoles(), NOT_ELIGIBLE_FOR_COMMISSION_ROLES).isEmpty()) {
            AffiliateCommission affiliateCommission = new AffiliateCommission();
            affiliateCommission.setPaid(null);
            affiliateCommission.setAffiliate(affiliateUser);
            affiliateCommission.setAffiliateCommValue(commission.value);
            affiliateCommission.setSalary(commission.salary);
            affiliateCommission.setValueType(commission.valueType);
            purchase.setAffiliateCommission(affiliateCommission);
        }
    }

    public void fillVendorCommission(Purchase purchase) {
        PolicyMeta policyMeta = purchase.getPolicyMeta();
        Commission commission;
        Vendor vendor = purchase.getPolicyMeta().getVendor();
        if (policyMeta.getPercentType().getId() != PercentType.NONE.getId()) {
            policyMeta = policyMetaService.getCached(policyMeta.getId());
            commission = calculateCommission(policyMeta.getPercentType(), policyMeta.getPercentInfo(), purchase);
        //Vendor commission is deprecated
        /*} else if (vendor.getPercentType().getId() != PercentType.NONE.getId()) {
            vendor = vendorService.getById(vendor.getId());
            commission = calculateCommission(vendor.getPercentType(), vendor.getPercentInfo(), purchase);
        */
        } else {
            commission = new Commission();
        }
        VendorCommission vendorCommission = new VendorCommission();
        vendorCommission.setExpectedCommission(commission.salary);
        vendorCommission.setCommissionValue(commission.value);
        vendorCommission.setValueType(commission.valueType);
        vendorCommission.setConfirm(false);
        vendorCommission.setVendor(vendor);
        purchase.setVendorCommission(vendorCommission);
    }

    private Commission calculateRefCommission(PercentType percentType, List<PercentInfo> percentInfoList, Purchase purchase) {
        if (percentType.getId() == PercentType.NONE.getId()) {
            SystemSettings systemSettings = systemSettingsService.getDefault();
            percentType = systemSettings.getDefaultLinkPercentType();
            percentInfoList = systemSettings.getDefaultLinkPercentInfo();
        }
        return calculateCommission(percentType, percentInfoList, purchase);
    }

    //add calculated type
    private Commission calculateCommission(PercentType percentType, List<PercentInfo> percentInfoList, Purchase purchase) {
        BigDecimal totalPrice = purchase.getTotalPrice();
        Commission commission = new Commission();
        if (percentType == null || CollectionUtils.isEmpty(percentInfoList)) {
            return commission;
        }
        switch (percentType) {
            case FIXED:
                commission.intValue = Optional.ofNullable(percentInfoList.get(0).getValue());
                if (commission.intValue.isPresent()) {
                    commission.value = commission.intValue.get().toString();
                    commission.salary = BigDecimal.valueOf(commission.intValue.get());
                }
                break;
            case RANGE_FIXED:
                for (PercentInfo percentInfo : percentInfoList) {
                    if (percentInfo.isValueBetween(totalPrice.intValue())) {
                        commission.intValue = Optional.ofNullable(percentInfo.getValue());
                        commission.value = percentInfo.getValue().toString();
                        break;
                    }
                }
                if (commission.intValue.isPresent()) {
                    commission.salary = BigDecimal.valueOf(commission.intValue.get());
                }
                break;
            case PERCENT:
                commission.intValue = Optional.ofNullable(percentInfoList.get(0).getValue());
                commission.value = percentInfoList.get(0).getValue().toString();
                if (commission.intValue.isPresent()) {
                    commission.salary = totalPrice.multiply(BigDecimal.valueOf(commission.intValue.get()))
                            .divide(BigDecimal.valueOf(100))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            case RANGE_PERCENT:
                for (PercentInfo percentInfo : percentInfoList) {
                    if (percentInfo.isValueBetween(totalPrice.intValue())) {
                        commission.intValue = Optional.ofNullable(percentInfo.getValue());
                        commission.value = percentInfo.getValue().toString();
                        break;
                    }
                }
                if (commission.intValue.isPresent()) {
                    commission.salary = totalPrice.multiply(BigDecimal.valueOf(commission.intValue.get()))
                            .divide(BigDecimal.valueOf(100))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                break;
            case CALCULATED:
                BigDecimal dynamicValue = calculateDynamicCommission(percentInfoList.get(0).getTextValue(), purchase.getQuoteRequest(), purchase.getTotalPrice().doubleValue());
                commission.value = dynamicValue.toString();
                commission.salary = dynamicValue;
                break;
        }
        commission.valueType = percentType.getCommissionValueType();
        return commission;
    }

    public BigDecimal calculateDynamicCommission(String script, QuoteRequest quoteRequest, Double price) {
        String destinationCountry = quoteRequest.getDestinationCountry().name();
        Double tripCost = quoteRequest.getTripCost().doubleValue();
        String departDate = DateUtil.getLocalDateStr(quoteRequest.getDepartDate());
        String returnDate = DateUtil.getLocalDateStr(quoteRequest.getReturnDate());
        String residenceState = quoteRequest.getResidentState() != null ? quoteRequest.getResidentState().name() : "";
        String residenceCountry = quoteRequest.getResidentCountry().name();
        String citizenship = quoteRequest.getCitizenCountry().name();
        StringJoiner sj = new StringJoiner(",", "[", "]");
        for (GenericTraveler traveler : quoteRequest.getTravelers()) {
            sj.add(traveler.getAge().toString());
        }
        List<Integer> ages = quoteRequest.getTravelers().stream().map(GenericTraveler::getAge).collect(Collectors.toList());
        BigDecimal resultVal = BigDecimal.ZERO;
        try {
            Object result = invokeGetCommissionFunction(script, price, destinationCountry, tripCost, departDate, returnDate,
                    residenceState, residenceCountry, citizenship, ages);
            resultVal = new BigDecimal((Double) result);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
            return resultVal;
        }
        return resultVal;
    }

    public Object invokeGetCommissionFunction(String script, Double price, String destinationCountry, Double tripCost, String departDate, String returnDate,
                                              String residenceState, String residenceCountry, String citizenShip, List<Integer> ages) throws ScriptException, NoSuchMethodException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(CALCULATE_COMMISSION_FUNCTION.replace("${script}", script));
        Invocable invocable = (Invocable) engine;
        return invocable.invokeFunction("getCommission", price, destinationCountry, tripCost,
                departDate, returnDate, residenceState, residenceCountry, citizenShip, ages);
    }

    private static final class Commission {
        Optional<Integer> intValue = Optional.empty();
        String value = "0";
        BigDecimal salary = new BigDecimal(0);
        CommissionValueType valueType = CommissionValueType.FIX;
    }
}
