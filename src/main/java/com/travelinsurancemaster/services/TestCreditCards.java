package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.common.CardType;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ritchie on 4/13/15.
 */
@Component
public class TestCreditCards {
    private Map<String, CreditCard> creditCardMap = new HashMap<>();

    public TestCreditCards() {
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 2);

        // add Credit Cards
        CreditCard allianzCC = new CreditCard("Vickie Stone", "05", year, 4012000033330026L, CardType.VISA, "24112", "");
        allianzCC.setCcAddress("155 A P Hill Road");
        allianzCC.setCcCity("Martinsville");
        allianzCC.setCcCountry(CountryCode.US);
        creditCardMap.put(ApiVendor.Allianz, allianzCC);
        creditCardMap.put(ApiVendor.CSA, new CreditCard("George Smith", "08", year, 4111111111111111L, CardType.VISA, "921236789", "111"));
        CreditCard iTravelInsuredCard = new CreditCard("George Smith", "10", year, 4112344112344113L, CardType.VISA, "921236789", "000");
        iTravelInsuredCard.setCcAddress("address");
        iTravelInsuredCard.setCcCity("city");
        iTravelInsuredCard.setCcCountry(CountryCode.US);
        iTravelInsuredCard.setCcStateCode(StateCode.AA);
        creditCardMap.put(ApiVendor.ITravelInsured, iTravelInsuredCard);
        creditCardMap.put(ApiVendor.TravelInsured, new CreditCard("John Doe", "09", year, 4007000000027L, CardType.VISA, "921236789", "123"));
        CreditCard sevenCornersCard = new CreditCard("Test Testerson", "12", year, 4111111111111111L, CardType.VISA, "12345", "000");
        creditCardMap.put(ApiVendor.SevenCorners, sevenCornersCard);
        sevenCornersCard.setCcStateCode(StateCode.CA);
        sevenCornersCard.setCcCountry(CountryCode.US);
        sevenCornersCard.setCcAddress("53rd Street");
        sevenCornersCard.setCcPhone("1234567890");
        sevenCornersCard.setCcCity("City");
        CreditCard tripMateCard = new CreditCard("Erica Boling", "02", year, 4111111111111111L, CardType.VISA, "67037", "123");
        creditCardMap.put(ApiVendor.TravelSafe, tripMateCard);
        creditCardMap.put(ApiVendor.MHRoss, tripMateCard);
        tripMateCard.setCcAddress("83 Forest Drive");
        tripMateCard.setCcPhone("9082272963");
        tripMateCard.setCcStateCode(StateCode.NJ);
        tripMateCard.setCcCity("Piscataway");
        tripMateCard.setCcCountry(CountryCode.US);
        CreditCard trawickCard = new CreditCard("Test Card", "12", year, 4111111111111111L, CardType.VISA, "36693", "999");
        trawickCard.setCcStateCode(StateCode.AL);
        trawickCard.setCcCity("Mobile");
        trawickCard.setCcAddress("123 Main St");
        trawickCard.setCcCountry(CountryCode.US);
        creditCardMap.put(ApiVendor.Trawick, trawickCard);
        CreditCard travelGuardCard = new CreditCard("Steve Harris", "12", year, 4111111111111111L, CardType.VISA, "40583", "333");
        travelGuardCard.setCcStateCode(StateCode.CA);
        travelGuardCard.setCcCity("Arcata");
        travelGuardCard.setCcAddress("54 Street ");
        travelGuardCard.setCcPhone("9086587845");
        travelGuardCard.setCcCountry(CountryCode.US);
        creditCardMap.put(ApiVendor.TravelGuard, travelGuardCard);
        CreditCard hCCMIScard = new CreditCard("Vickie Stone", "05", year, 4111111111111111L, CardType.VISA, "24112", "000");
        hCCMIScard.setCcStateCode(StateCode.CA);
        hCCMIScard.setCcCity("City");
        hCCMIScard.setCcAddress("53rd Street");
        hCCMIScard.setCcPhone("9086587845");
        hCCMIScard.setCcCountry(CountryCode.US);
        creditCardMap.put(ApiVendor.HCCMedicalInsuranceServices, hCCMIScard);
        CreditCard travelexInsuranceCard = new CreditCard("TEST TEST", "11", year, 1111L, CardType.VISA, "", "548");
        // TODO: update token for 4111-1111-1111-1111 Visa
        travelexInsuranceCard.setTokenCcNumber("[*****]");
        travelexInsuranceCard.setCcStateCode(StateCode.FL);
        travelexInsuranceCard.setCcCity("City");
        travelexInsuranceCard.setCcAddress("53rd Street");
        travelexInsuranceCard.setCcPhone("9086587845");
        travelexInsuranceCard.setCcCountry(CountryCode.US);
        creditCardMap.put(ApiVendor.TravelexInsurance, travelexInsuranceCard);
        CreditCard hTHTravelInsuranceCard = new CreditCard("Test Testerson", "12", "25", 5424000000000015L, CardType.VISA, "90003", "000");
        hTHTravelInsuranceCard.setCcAddress("53rd Street");
        hTHTravelInsuranceCard.setCcCity("City");
        hTHTravelInsuranceCard.setCcStateCode(StateCode.CA);
        hTHTravelInsuranceCard.setCcCountry(CountryCode.US);
        creditCardMap.put(ApiVendor.HTHTravelInsurance, hTHTravelInsuranceCard);
        CreditCard usaAssistCard = new CreditCard("Name", "12", year, 5424000000000015L, CardType.VISA, "90003", "000");
        usaAssistCard.setCcAddress("Street");
        usaAssistCard.setCcCity("City");
        usaAssistCard.setCcStateCode(StateCode.CA);
        usaAssistCard.setCcCountry(CountryCode.US);
        creditCardMap.put(ApiVendor.UsaAssist, usaAssistCard);
        CreditCard roamRightCard = new CreditCard("Joe Traveler", "02", year, 4111111111111111L, CardType.VISA, "", "123");
        roamRightCard.setCcAddress("125125 test st");
        roamRightCard.setCcCity("Towson");
        roamRightCard.setCcZipCode("21086");
        creditCardMap.put(ApiVendor.RoamRight, roamRightCard);
        //Berkshire Hathaway Travel Protection
        CreditCard bhtpCreditCard = new CreditCard("Joe Traveler", "05",year,4111111111111111L, CardType.VISA,"32301","678");
        bhtpCreditCard.setCcAddress("Park avenue 45");
        bhtpCreditCard.setCcStateCode(StateCode.FL);
        bhtpCreditCard.setCcCity("Miami");
        bhtpCreditCard.setCcCountry(CountryCode.US);
        creditCardMap.put(ApiVendor.BHTravelProtection, bhtpCreditCard);
        //GlobalAlert
        CreditCard gaCreditCard = new CreditCard("Joe Traveler", "08", year,4111111111111111L, CardType.VISA,"32301","123");
        gaCreditCard.setCcAddress("Park avenue 45");
        gaCreditCard.setCcStateCode(StateCode.FL);
        gaCreditCard.setCcCountry(CountryCode.US);
        gaCreditCard.setCcCity("Miami");
        creditCardMap.put(ApiVendor.GlobalAlert, gaCreditCard);

    }

    /**
     * @return credit card data that is used for test purchase.
     */
    public CreditCard getCreditCardByVendorCode(String vendorCode) {
        return creditCardMap.get(vendorCode);
    }
}
