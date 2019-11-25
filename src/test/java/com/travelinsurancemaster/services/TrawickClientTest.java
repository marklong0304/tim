package com.travelinsurancemaster.services;

import com.travelinsurancemaster.TestConfig;
import com.travelinsurancemaster.TestUtils;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.webservice.trawick.TrawickOrder;
import com.travelinsurancemaster.services.clients.TrawickClient;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

;
;

/**
 * Created by ritchie on 2/5/15.
 */
@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@Ignore
public class TrawickClientTest {

    private static final Logger log = LoggerFactory.getLogger(TrawickClientTest.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    private TrawickClient trawickClient;

    /** ------------------------------------ International Travel Plans ------------------------------------ **/

    /* Safe Travels International  */

    @Test
    public void quoteProduct19() {
        quoteInternationalTravelPlans(TrawickClient.SAFE_TRAVELS_INTERNATIONAL_PLAN_CODE, 9.672f);
    }

    @Test
    @Ignore
    public void orderProduct19() {
        orderInternationalTravelPlans(TrawickClient.SAFE_TRAVELS_INTERNATIONAL_PLAN_CODE, 9.672f);
    }

    /* Safe Travels International Cost Saver  */

    @Test
    public void quoteProduct30() {
        quoteInternationalTravelPlans(TrawickClient.SAFE_TRAVELS_INTERNATIONAL_COST_SAVER_PLAN_CODE, 8.32f);
    }

    @Test
    @Ignore
    public void orderProduct30() {
        orderInternationalTravelPlans(TrawickClient.SAFE_TRAVELS_INTERNATIONAL_COST_SAVER_PLAN_CODE, 8.32f);
    }

    private void quoteInternationalTravelPlans(String planCode, float price) {
        MultiValueMap<String, String> params = getBaseQuoteParams();
        fillQuoteParamsForInternationalTravelPlans(planCode, params);
        TrawickOrder response = trawickClient.quote(params);
        Assert.assertTrue(isPriceEquals(response.getTotalPrice(), price));
        Assert.assertNotNull(response.getQuoteNumber());
        Assert.assertNotNull(response.getBuyNowLink());
        Assert.assertEquals(response.getOrderStatusCode(), "0");
    }

    private void orderInternationalTravelPlans(String planCode, float price) {
        MultiValueMap<String, String> params = getBaseOrderParams();
        fillQuoteParamsForInternationalTravelPlans(planCode, params);
        TrawickOrder response = trawickClient.order(params);
        Assert.assertTrue(response.getOrderStatusCode().equals("1") || response.getOrderStatusCode().equals("2"));
        Assert.assertTrue(isPriceEquals(response.getTotalPrice(), price));
        Assert.assertNotNull(response.getOrderRequestId());
    }

    private void fillQuoteParamsForInternationalTravelPlans(String planCode, MultiValueMap<String, String> params){
        params.add("product", planCode);
        params.add("policy_max", "50000");
        params.add("destination", CountryCode.ES.name());
        params.add("deductible", "0");
        params.add("country", CountryCode.AL.name());
        params.add("ad-d_upgrade", "25000");
        params.add("sports", "no");
    }

    /** ----------------------------------- Travel Plans Inbound to the US ----------------------------------- **/

    /* Safe Travels USA  */

    @Test
    public void quoteProduct16() {
        quoteTravelPlansInboundToUS(TrawickClient.SAFE_TRAVELS_USA_PLAN_CODE, 13.648f);
    }

    @Test
    @Ignore
    public void orderProduct16() {
        orderTravelPlansInboundToUS(TrawickClient.SAFE_TRAVELS_USA_PLAN_CODE, 13.648f);
    }

    /* Safe Travels USA Cost Saver  */

    @Test
    public void quoteProduct63() {
        quoteTravelPlansInboundToUS(TrawickClient.SAFE_TRAVELS_USA_COST_SAVER_PLAN_CODE, 12.08f);
    }

    @Test
    @Ignore
    public void orderProduct63() {
        orderTravelPlansInboundToUS(TrawickClient.SAFE_TRAVELS_USA_COST_SAVER_PLAN_CODE, 12.08f);
    }

    /* Safe Travels USA Comprehensive  */

    @Test
    public void quoteProduct64() {
        quoteTravelPlansInboundToUS(TrawickClient.SAFE_TRAVELS_USA_COMPREHENSIVE_PLAN_CODE, 13.76f);
    }

    @Test
    @Ignore
    public void orderProduct64() {
        orderTravelPlansInboundToUS(TrawickClient.SAFE_TRAVELS_USA_COMPREHENSIVE_PLAN_CODE, 13.76f);
    }

    private void quoteTravelPlansInboundToUS(String planCode, float price) {
        MultiValueMap<String, String> params = getBaseQuoteParams();
        fillQuoteParamsForTravelPlansInboundToUS(planCode, params);
        TrawickOrder response = trawickClient.quote(params);
        Assert.assertTrue(isPriceEquals(response.getTotalPrice(), price));
        Assert.assertNotNull(response.getQuoteNumber());
        Assert.assertNotNull(response.getBuyNowLink());
        Assert.assertEquals(response.getOrderStatusCode(), "0");
    }

    private void orderTravelPlansInboundToUS(String planCode, float price) {
        MultiValueMap<String, String> params = getBaseOrderParams();
        fillQuoteParamsForTravelPlansInboundToUS(planCode, params);
        TrawickOrder response = trawickClient.order(params);
        Assert.assertTrue(response.getOrderStatusCode().equals("1") || response.getOrderStatusCode().equals("2"));
        Assert.assertTrue(isPriceEquals(response.getTotalPrice(), price));
        Assert.assertNotNull(response.getOrderRequestId());
    }

    private void fillQuoteParamsForTravelPlansInboundToUS(String planCode, MultiValueMap<String, String> params){
        params.add("product", planCode);
        params.add("policy_max", "50000");
        params.add("destination", CountryCode.US.name());
        params.add("deductible", "0");
        params.add("country", CountryCode.ES.name());
        params.add("ad-d_upgrade", "50000");
        params.add("sports", "no");
        params.add("home_country", "no");
    }

    /** ------------------------------------- Trip Protection Plans ------------------------------------- **/

    /* Safe Travels USA Trip Cancellation  */

    @Test
    public void quoteProduct28(){
        quoteTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_USA_TRIP_CANCELLATION_PLAN_CODE, CountryCode.US.name(), CountryCode.ES.name(), 19.00f);
    }

    @Test
    @Ignore
    public void orderProduct28(){
        orderTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_USA_TRIP_CANCELLATION_PLAN_CODE, CountryCode.US.name(), CountryCode.ES.name(),19.00f);
    }

    /* Safe Travels Multinational Trip Cancellation  */

    @Test
    public void quoteProduct48(){
        quoteTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_MULTINATIONAL_TRIP_CANCELLATION_PLAN_CODE, CountryCode.CA.name(), CountryCode.ES.name(),17.00f);
    }

    @Test
    @Ignore
    public void orderProduct48(){
        orderTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_MULTINATIONAL_TRIP_CANCELLATION_PLAN_CODE, CountryCode.CA.name(), CountryCode.ES.name(),17.00f);
    }

    /* Safe Travels 3 in 1  */

    @Test
    public void quoteProduct65(){
        quoteTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_3_IN_1_PLAN_CODE, CountryCode.ES.name(), CountryCode.US.name(), 12.50f);
    }

    @Test
    @Ignore
    public void orderProduct65(){
        orderTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_3_IN_1_PLAN_CODE,  CountryCode.ES.name(), CountryCode.US.name(), 12.50f);
    }

    /* Safe Travels Single Trip  */

    @Test
    public void quoteProduct66(){
        quoteTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_SINGLE_TRIP_PLAN_CODE, CountryCode.ES.name(), CountryCode.US.name(), 13.86f);
    }

    @Test
    public void orderProduct66(){
        quoteTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_SINGLE_TRIP_PLAN_CODE, CountryCode.ES.name(), CountryCode.US.name(), 13.86f);
    }

    /* Safe Travels First Class  */

    @Test
    public void quoteProduct67(){
        quoteTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE, CountryCode.ES.name(), CountryCode.US.name(), 25.41f);
    }

    @Test
    @Ignore
    public void orderProduct67(){
        orderTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_FIRST_CLASS_PLAN_CODE, CountryCode.ES.name(), CountryCode.US.name(), 25.41f);
    }

    @Test
    public void quoteProduct72() {
        quoteTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_VACANTIONER_PLAN_CODE, CountryCode.ES.name(), CountryCode.US.name(), 44f);
    }

    @Test
    @Ignore
    public void orderProduct72() {
        orderTravelTripProtectionPlans(TrawickClient.SAFE_TRAVELS_VACANTIONER_PLAN_CODE, CountryCode.ES.name(), CountryCode.US.name(), 44f);
    }

    private void quoteTravelTripProtectionPlans(String planCode, String destination, String country, float price) {
        MultiValueMap<String, String> params = getBaseQuoteParams();
        fillQuoteParamsForTripProtectionPlans(planCode, params, destination, country);
        TrawickOrder response = trawickClient.quote(params);
        Assert.assertTrue(isPriceEquals(response.getTotalPrice(), price));
        Assert.assertNotNull(response.getQuoteNumber());
        Assert.assertNotNull(response.getBuyNowLink());
        Assert.assertEquals(response.getOrderStatusCode(), "0");
    }

    private void orderTravelTripProtectionPlans(String planCode, String destination, String country, float price) {
        MultiValueMap<String, String> params = getBaseOrderParams();
        fillQuoteParamsForTripProtectionPlans(planCode, params, destination, country);
        TrawickOrder response = trawickClient.order(params);
        Assert.assertTrue(response.getOrderStatusCode().equals("1") || response.getOrderStatusCode().equals("2"));
        Assert.assertTrue(isPriceEquals(response.getTotalPrice(), price));
        Assert.assertNotNull(response.getOrderRequestId());
    }

    private void fillQuoteParamsForTripProtectionPlans(String planCode, MultiValueMap<String, String> params, String destination, String country) {
        params.add("product", planCode);
        params.add("medical_limit", "50000");
        params.add("destination", destination);
        params.add("deductible", "0");
        params.add("country", country);
        params.add("ad-d_upgrade", "50000");
        params.add("sports", "no");
        if (StringUtils.equals(planCode, TrawickClient.SAFE_TRAVELS_3_IN_1_PLAN_CODE)) {
            params.add("trip_cost_per_person", "250");
        }
    }

    /* Base block */

    private MultiValueMap<String, String> getBaseQuoteParams(){
        // base quote parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("agent_id", "1");
        params.add("eff_date", TestUtils.getFormattedDate(TestUtils.getIncrementedDate(2), DATE_FORMAT));
        params.add("term_date", TestUtils.getFormattedDate(TestUtils.getIncrementedDate(9), DATE_FORMAT));
        params.add("dob1", "04/10/1990");
        return params;
    }

    private MultiValueMap<String, String> getBaseOrderParams() {
        MultiValueMap<String, String> params = getBaseQuoteParams();
        // base order parameters
        params.add("completeOrder", "true");
        params.add("t1First", UUID.randomUUID().toString());
        params.add("t1Middle", ""); // optional
        params.add("t1Last", UUID.randomUUID().toString());
        params.add("t1Gender", "Male");
        params.add("mainEmail", "test@gmail.com");
        params.add("street", "300 Fairhope Ave");
        params.add("city", "Fairhope");
        params.add("state", "AL");
        params.add("zip", "36526");
        params.add("citizenship", CountryCode.US.name());
        params.add("phone", "2516891725");
        params.add("homecountry", CountryCode.ES.name());
        params.add("cc_name", "Test Card");
        params.add("cc_street", "123 Main St");
        params.add("cc_city", "Mobile");
        params.add("cc_statecode", StateCode.WA.name());
        params.add("cc_postalcode", "36693");
        params.add("cc_country", "US");
        params.add("cc_number", "4111111111111111");
        params.add("cc_month", "12");
        params.add("cc_year", "2014");
        params.add("cc_cvv", "999");
        return params;
    }

    private boolean isPriceEquals(String apiStrVal, float val){
        try {
            float eps = 0.01f;
            Float apiVal = Float.valueOf(apiStrVal);
            return Math.abs(apiVal - val) < eps;
        } catch (NumberFormatException e){
            log.debug("Incorrect price format");
            return false;
        } catch (Exception e){
            return false;
        }
    }
}
