package com.travelinsurancemaster.services.clients;

import com.travelinsurancemaster.clients.travelguard.GetQuote;
import com.travelinsurancemaster.clients.travelguard.GetQuoteResponse;
import com.travelinsurancemaster.clients.travelguard.Purchase;
import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.PlanType;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyMetaCode;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.model.webservice.travelguard.*;
import com.travelinsurancemaster.services.AbstractSoapClient;
import com.travelinsurancemaster.services.InsuranceMasterApiProperties;
import com.travelinsurancemaster.services.PolicyMetaCategoryValueService;
import com.travelinsurancemaster.services.PolicyMetaCodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.xml.transform.StringResult;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ritchie on 3/4/15.
 */
public class TravelGuardClient extends AbstractSoapClient {

    private static final String DEFAULT_UPSALE_VALUE = "";
    private final String CERTIFICATE_TEMPLATE = "https://webservices.travelguard.com/Product/FileRetrieval.aspx?StateCode=${stateCode}&ProductCode=${productCode}&PlanCode=${planCode}&CountryCode=US&FileType=PROD_PLAN_DOC";

    @Autowired
    InsuranceMasterApiProperties apiProperties;

    @Autowired
    private PolicyMetaCategoryValueService policyMetaCategoryValueService;

    @Autowired
    private PolicyMetaCodeService policyMetaCodeService;

    private static final Logger log = LoggerFactory.getLogger(TravelGuardClient.class);
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    public static final String QUOTE = "QUOTE";
    public static final String MERCHANT_XML_QUOTE = "MerchantXMLQuote";
    public static final String CODE_SEPARATOR = "-";
    public static final String PURCHASE = "PURCHASE";
    public static final String MERCHANT_XML_PURCHASE = "MerchantXMLPurchase";
    public static final String ONLINE = "ONLINE";
    public static final String YES = "YES";
    private Map<String, Integer> optionalPackagesMap;

    private static final String GOLD_406700_CODE = "406700-117705";
    private static final String GOLD_806700_CODE = "806700-132543";
    private static final String GOLD_CA6700_CODE = "CA6700-234740";
    private static final String GOLD_AK6700_CODE = "AK6700-216275";
    private static final String GOLD_CO6700_CODE = "CO6700-216350";
    private static final String GOLD_MT6700_CODE = "MT6700-234252";
    private static final String GOLD_NH6700_CODE = "NH6700-235543";
    private static final String GOLD_NW6700_CODE = "NW6700-157176";
    private static final String GOLD_PI6700_CODE = "PI6700-158334";
    private static final String GOLD_KS6700_CODE = "KS6700-234680";
    private static final String GOLD_MD6700_CODE = "MD6700-235455";
    private static final String PLATINUM_406600_CODE = "406600-118437";
    private static final String PLATINUM_806600_CODE = "806600-133550";
    private static final String PLATINUM_AK6600_CODE = "AK6600-216258";
    private static final String PLATINUM_CO6600_CODE = "CO6600-216314";
    private static final String PLATINUM_MT6600_CODE = "MT6600-234234";
    private static final String PLATINUM_NH6600_CODE = "NH6600-235457";
    private static final String PLATINUM_NW6600_CODE = "NW6600-157345";
    private static final String PLATINUM_PI6600_CODE = "PI6600-158353";
    private static final String PLATINUM_CA6600_CODE = "CA6600-234688";
    private static final String PLATINUM_KS6600_CODE = "KS6600-234652";
    private static final String PLATINUM_MD6600_CODE = "MD6600-235417";
    private static final String SILVER_408838_CODE = "408838-118861";
    private static final String SILVER_808838_CODE = "808838-134085";
    private static final String SILVER_AK8838_CODE = "AK8838-216320";
    private static final String SILVER_MT8838_CODE = "MT8838-234260";
    private static final String SILVER_NH8838_CODE = "NH8838-235560";
    private static final String SILVER_NW8838_CODE = "NW8838-157508";
    private static final String SILVER_PI8838_CODE = "PI8838-158313";
    private static final String SILVER_CO8838_CODE = "CO8838-216400";
    private static final String SILVER_CA8838_CODE = "CA8838-234778";
    private static final String SILVER_KS8838_CODE = "KS8838-234697";
    private static final String SILVER_MD8838_CODE = "MD8838-235467";
    private static final String BASIC_407838_CODE = "407838-118768";
    private static final String BASIC_807838_CODE = "807838-133661";
    private static final String BASIC_CA7838_CODE = "CA7838-234772";
    private static final String BASIC_CO7838_CODE = "CO7838-216360";
    private static final String BASIC_KS7838_CODE = "KS7838-234728";
    private static final String BASIC_MD7838_CODE = "MD7838-235499";
    private static final String BASIC_MT7838_CODE = "MT7838-234243";
    private static final String BASIC_NH7838_CODE = "NH7838-235573";
    private static final String BASIC_NW7838_CODE = "NW7838-153589";
    private static final String BASIC_PI7838_CODE = "PI7838-158301";
    private static final String PLUS_101838_CODE = "101838-245421";
    private static final String PLUS_CA1838_CODE = "CA1838-245423";
    private static final String PLUS_IN1838_CODE = "IN1838-245428";
    private static final String PLUS_KS1838_CODE = "KS1838-245422";
    private static final String PLUS_MT1838_CODE = "MT1838-245427";
    private static final String PLUS_NH1838_CODE = "NH1838-245468";
    private static final String PLUS_NY1838_CODE = "NY1838-245426";


    public TravelGuardClient() {
        optionalPackagesMap = new HashMap<>();
        optionalPackagesMap.put(GOLD_406700_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 17014);
        optionalPackagesMap.put(GOLD_406700_CODE + CategoryCodes.RENTAL_CAR, 17015);
        optionalPackagesMap.put(GOLD_406700_CODE + CategoryCodes.EMERGENCY_MEDICAL, 17016);
        optionalPackagesMap.put(GOLD_806700_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 17587);
        optionalPackagesMap.put(GOLD_806700_CODE + CategoryCodes.CANCEL_FOR_WORK_REASONS, 17588);
        optionalPackagesMap.put(GOLD_806700_CODE + CategoryCodes.RENTAL_CAR, 17586);
        optionalPackagesMap.put(GOLD_806700_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 17589);
        optionalPackagesMap.put(GOLD_806700_CODE + CategoryCodes.EMERGENCY_MEDICAL, 17590);
        optionalPackagesMap.put(GOLD_CA6700_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 20241);
        optionalPackagesMap.put(GOLD_CA6700_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20244);
        optionalPackagesMap.put(GOLD_CA6700_CODE + CategoryCodes.EMERGENCY_MEDICAL, 20728);
        optionalPackagesMap.put(GOLD_CA6700_CODE + CategoryCodes.RENTAL_CAR, 20242);
        optionalPackagesMap.put(GOLD_MD6700_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 20276);
        optionalPackagesMap.put(GOLD_MD6700_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20279);
        optionalPackagesMap.put(GOLD_MD6700_CODE + CategoryCodes.EMERGENCY_MEDICAL, 20730);
        optionalPackagesMap.put(GOLD_MD6700_CODE + CategoryCodes.RENTAL_CAR, 20277);
        optionalPackagesMap.put(GOLD_KS6700_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 20230);
        optionalPackagesMap.put(GOLD_KS6700_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20232);
        optionalPackagesMap.put(GOLD_KS6700_CODE + CategoryCodes.EMERGENCY_MEDICAL, 20725);
        optionalPackagesMap.put(GOLD_AK6700_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 19738);
        optionalPackagesMap.put(GOLD_AK6700_CODE + CategoryCodes.RENTAL_CAR, 19739);
        optionalPackagesMap.put(GOLD_AK6700_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 19741);
        optionalPackagesMap.put(GOLD_AK6700_CODE + CategoryCodes.EMERGENCY_MEDICAL, 20727);
        optionalPackagesMap.put(GOLD_CO6700_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 19758);
        optionalPackagesMap.put(GOLD_CO6700_CODE + CategoryCodes.RENTAL_CAR, 19759);
        optionalPackagesMap.put(GOLD_CO6700_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 19761);
        optionalPackagesMap.put(GOLD_CO6700_CODE + CategoryCodes.MEDICAL_EVACUATION, 19760);
        optionalPackagesMap.put(GOLD_CO6700_CODE + CategoryCodes.EMERGENCY_MEDICAL, 20732);
        optionalPackagesMap.put(GOLD_MT6700_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 20209);
        optionalPackagesMap.put(GOLD_MT6700_CODE + CategoryCodes.RENTAL_CAR, 20208);
        optionalPackagesMap.put(GOLD_NH6700_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 20304);
        optionalPackagesMap.put(GOLD_NH6700_CODE + CategoryCodes.RENTAL_CAR, 20305);
        optionalPackagesMap.put(GOLD_NH6700_CODE + CategoryCodes.EMERGENCY_MEDICAL, 20731);
        optionalPackagesMap.put(GOLD_NW6700_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 18300);
        optionalPackagesMap.put(GOLD_NW6700_CODE + CategoryCodes.RENTAL_CAR, 18301);
        optionalPackagesMap.put(GOLD_NW6700_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 18303);
        optionalPackagesMap.put(GOLD_NW6700_CODE + CategoryCodes.EMERGENCY_MEDICAL, 20729);
        optionalPackagesMap.put(GOLD_PI6700_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 18343);
        optionalPackagesMap.put(GOLD_PI6700_CODE + CategoryCodes.RENTAL_CAR, 18344);
        optionalPackagesMap.put(GOLD_PI6700_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 18346);
        optionalPackagesMap.put(GOLD_PI6700_CODE + CategoryCodes.EMERGENCY_MEDICAL, 20726);
        optionalPackagesMap.put(PLATINUM_406600_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 17098);
        optionalPackagesMap.put(PLATINUM_406600_CODE + CategoryCodes.RENTAL_CAR, 17101);
        optionalPackagesMap.put(PLATINUM_406600_CODE + CategoryCodes.EMERGENCY_MEDICAL, 17099);
        optionalPackagesMap.put(PLATINUM_806600_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 17642);
        optionalPackagesMap.put(PLATINUM_806600_CODE + CategoryCodes.CANCEL_FOR_WORK_REASONS, 17643);
        optionalPackagesMap.put(PLATINUM_806600_CODE + CategoryCodes.RENTAL_CAR, 17644);
        optionalPackagesMap.put(PLATINUM_806600_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 17645);
        optionalPackagesMap.put(PLATINUM_806600_CODE + CategoryCodes.EMERGENCY_MEDICAL, 17646);
        optionalPackagesMap.put(PLATINUM_AK6600_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 19732);
        optionalPackagesMap.put(PLATINUM_AK6600_CODE + CategoryCodes.RENTAL_CAR, 19733);
        optionalPackagesMap.put(PLATINUM_AK6600_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 19734);
        optionalPackagesMap.put(PLATINUM_AK6600_CODE + CategoryCodes.EMERGENCY_MEDICAL, 19735);
        optionalPackagesMap.put(PLATINUM_CO6600_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 19745);
        optionalPackagesMap.put(PLATINUM_CO6600_CODE + CategoryCodes.RENTAL_CAR, 19746);
        optionalPackagesMap.put(PLATINUM_CO6600_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 19747);
        optionalPackagesMap.put(PLATINUM_CO6600_CODE + CategoryCodes.EMERGENCY_MEDICAL, 19748);
        optionalPackagesMap.put(PLATINUM_MT6600_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 20215);
        optionalPackagesMap.put(PLATINUM_MT6600_CODE + CategoryCodes.RENTAL_CAR, 20214);
        optionalPackagesMap.put(PLATINUM_NH6600_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 20282);
        optionalPackagesMap.put(PLATINUM_NH6600_CODE + CategoryCodes.RENTAL_CAR, 20301);
        optionalPackagesMap.put(PLATINUM_NW6600_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 18310);
        optionalPackagesMap.put(PLATINUM_NW6600_CODE + CategoryCodes.RENTAL_CAR, 18309);
        optionalPackagesMap.put(PLATINUM_NW6600_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 18307);
        optionalPackagesMap.put(PLATINUM_NW6600_CODE + CategoryCodes.EMERGENCY_MEDICAL, 18308);
        optionalPackagesMap.put(PLATINUM_PI6600_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 18351);
        optionalPackagesMap.put(PLATINUM_PI6600_CODE + CategoryCodes.RENTAL_CAR, 18352);
        optionalPackagesMap.put(PLATINUM_PI6600_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 18349);
        optionalPackagesMap.put(PLATINUM_PI6600_CODE + CategoryCodes.EMERGENCY_MEDICAL, 18350);
        optionalPackagesMap.put(PLATINUM_CA6600_CODE + CategoryCodes.RENTAL_CAR, 20226);
        optionalPackagesMap.put(PLATINUM_CA6600_CODE + CategoryCodes.EMERGENCY_MEDICAL, 20228);
        optionalPackagesMap.put(PLATINUM_CA6600_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 20225);
        optionalPackagesMap.put(PLATINUM_CA6600_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20227);
        optionalPackagesMap.put(PLATINUM_KS6600_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 20219);
        optionalPackagesMap.put(PLATINUM_KS6600_CODE + CategoryCodes.EMERGENCY_MEDICAL, 20221);
        optionalPackagesMap.put(PLATINUM_KS6600_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20220);
        optionalPackagesMap.put(PLATINUM_MD6600_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 20270);
        optionalPackagesMap.put(PLATINUM_MD6600_CODE + CategoryCodes.EMERGENCY_MEDICAL, 20273);
        optionalPackagesMap.put(PLATINUM_MD6600_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20272);
        optionalPackagesMap.put(PLATINUM_MD6600_CODE + CategoryCodes.RENTAL_CAR, 20271);
        optionalPackagesMap.put(BASIC_407838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 17116);
        optionalPackagesMap.put(BASIC_407838_CODE + CategoryCodes.RENTAL_CAR, 17114);
        optionalPackagesMap.put(BASIC_407838_CODE + CategoryCodes.EMERGENCY_MEDICAL, 17115);
        optionalPackagesMap.put(BASIC_807838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 17655);
        optionalPackagesMap.put(BASIC_807838_CODE + CategoryCodes.RENTAL_CAR, 17653);
        optionalPackagesMap.put(BASIC_807838_CODE + CategoryCodes.EMERGENCY_MEDICAL, 17654);
        optionalPackagesMap.put(BASIC_CA7838_CODE + CategoryCodes.RENTAL_CAR, 20252);
        optionalPackagesMap.put(BASIC_CA7838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20253);
        optionalPackagesMap.put(BASIC_CO7838_CODE + CategoryCodes.RENTAL_CAR, 19756);
        optionalPackagesMap.put(BASIC_CO7838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 19757);
        optionalPackagesMap.put(BASIC_KS7838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20239);
        optionalPackagesMap.put(BASIC_MD7838_CODE + CategoryCodes.RENTAL_CAR, 20289);
        optionalPackagesMap.put(BASIC_MD7838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20290);
        optionalPackagesMap.put(BASIC_MT7838_CODE + CategoryCodes.RENTAL_CAR, 20205);
        optionalPackagesMap.put(BASIC_NH7838_CODE + CategoryCodes.RENTAL_CAR, 20313);
        optionalPackagesMap.put(BASIC_NW7838_CODE + CategoryCodes.RENTAL_CAR, 18169);
        optionalPackagesMap.put(BASIC_NW7838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 18171);
        optionalPackagesMap.put(BASIC_PI7838_CODE + CategoryCodes.RENTAL_CAR, 18335);
        optionalPackagesMap.put(BASIC_PI7838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 18336);
        optionalPackagesMap.put(SILVER_408838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 17133);
        optionalPackagesMap.put(SILVER_408838_CODE + CategoryCodes.RENTAL_CAR, 17131);
        optionalPackagesMap.put(SILVER_408838_CODE + CategoryCodes.MEDICAL_EVACUATION, 17132);
        optionalPackagesMap.put(SILVER_808838_CODE + CategoryCodes.RENTAL_CAR, 17672);
        optionalPackagesMap.put(SILVER_808838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 17673);
        optionalPackagesMap.put(SILVER_808838_CODE + CategoryCodes.MEDICAL_EVACUATION, 17674);
        optionalPackagesMap.put(SILVER_AK8838_CODE + CategoryCodes.RENTAL_CAR, 19750);
        optionalPackagesMap.put(SILVER_AK8838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 19753);
        optionalPackagesMap.put(SILVER_AK8838_CODE + CategoryCodes.MEDICAL_EVACUATION, 19752);
        optionalPackagesMap.put(SILVER_MT8838_CODE + CategoryCodes.RENTAL_CAR, 20212);
        optionalPackagesMap.put(SILVER_NH8838_CODE + CategoryCodes.RENTAL_CAR, 20309);
        optionalPackagesMap.put(SILVER_NH8838_CODE + CategoryCodes.MEDICAL_EVACUATION, 20310);
        optionalPackagesMap.put(SILVER_NW8838_CODE + CategoryCodes.RENTAL_CAR, 18324);
        optionalPackagesMap.put(SILVER_NW8838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 18326);
        optionalPackagesMap.put(SILVER_NW8838_CODE + CategoryCodes.MEDICAL_EVACUATION, 18325);
        optionalPackagesMap.put(SILVER_PI8838_CODE + CategoryCodes.RENTAL_CAR, 18338);
        optionalPackagesMap.put(SILVER_PI8838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 18340);
        optionalPackagesMap.put(SILVER_PI8838_CODE + CategoryCodes.MEDICAL_EVACUATION, 18339);
        optionalPackagesMap.put(SILVER_CA8838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20250);
        optionalPackagesMap.put(SILVER_CA8838_CODE + CategoryCodes.MEDICAL_EVACUATION, 20249);
        optionalPackagesMap.put(SILVER_CA8838_CODE + CategoryCodes.RENTAL_CAR, 20248);
        optionalPackagesMap.put(SILVER_KS8838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20236);
        optionalPackagesMap.put(SILVER_KS8838_CODE + CategoryCodes.MEDICAL_EVACUATION, 20235);
        optionalPackagesMap.put(SILVER_MD8838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 20286);
        optionalPackagesMap.put(SILVER_MD8838_CODE + CategoryCodes.MEDICAL_EVACUATION, 20285);
        optionalPackagesMap.put(SILVER_MD8838_CODE + CategoryCodes.RENTAL_CAR, 20284);
        optionalPackagesMap.put(SILVER_CO8838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 19766);
        optionalPackagesMap.put(SILVER_CO8838_CODE + CategoryCodes.MEDICAL_EVACUATION, 19765);
        optionalPackagesMap.put(SILVER_CO8838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 19764);
        optionalPackagesMap.put(PLUS_101838_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 21056);
        optionalPackagesMap.put(PLUS_101838_CODE + CategoryCodes.RENTAL_CAR, 21058);
        optionalPackagesMap.put(PLUS_101838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 21057);
        optionalPackagesMap.put(PLUS_CA1838_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 21066);
        optionalPackagesMap.put(PLUS_CA1838_CODE + CategoryCodes.RENTAL_CAR, 21067);
        optionalPackagesMap.put(PLUS_CA1838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 21068);
        optionalPackagesMap.put(PLUS_IN1838_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 21073);
        optionalPackagesMap.put(PLUS_IN1838_CODE + CategoryCodes.RENTAL_CAR, 21075);
        optionalPackagesMap.put(PLUS_IN1838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 21074);
        optionalPackagesMap.put(PLUS_KS1838_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 21060);
        optionalPackagesMap.put(PLUS_KS1838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 21065);
        optionalPackagesMap.put(PLUS_MT1838_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 21076);
        optionalPackagesMap.put(PLUS_MT1838_CODE + CategoryCodes.RENTAL_CAR, 21077);
        optionalPackagesMap.put(PLUS_NH1838_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 21094);
        optionalPackagesMap.put(PLUS_NH1838_CODE + CategoryCodes.RENTAL_CAR, 21096);
        optionalPackagesMap.put(PLUS_NY1838_CODE + CategoryCodes.CANCEL_FOR_ANY_REASON, 21071);
        optionalPackagesMap.put(PLUS_NY1838_CODE + CategoryCodes.FLIGHT_ONLY_AD_AND_D, 21072);
    }

    public PolicyQuoteDetailsTG quote(PolicySpecificationTG policySpecification) {
        Jaxb2Marshaller jaxb2Marshaller = (Jaxb2Marshaller) getWebServiceTemplate().getMarshaller();
        StringResult stringResult = new StringResult();
        jaxb2Marshaller.marshal(policySpecification, stringResult);
        String quoteRequestXml = stringResult.toString();

        GetQuote quoteRequest = new GetQuote();
        quoteRequest.setXml(quoteRequestXml);
        GetQuoteResponse response = (GetQuoteResponse) getWebServiceTemplate().marshalSendAndReceive(quoteRequest,
                new SoapActionCallback(
                        "https://tgpolicy.websvcs.travelguard.com/GetQuote"));
        String quoteResult = response.getGetQuoteResult();
        StringReader stringReader = new StringReader(quoteResult);
        Source source = new StreamSource(stringReader);
        return (PolicyQuoteDetailsTG) jaxb2Marshaller.unmarshal(source);
    }

    public PolicyPurchaseDetailsTG purchase(PolicySpecificationTG policySpecification) {
        Jaxb2Marshaller jaxb2Marshaller = (Jaxb2Marshaller) getWebServiceTemplate().getMarshaller();
        StringResult stringResult = new StringResult();
        jaxb2Marshaller.marshal(policySpecification, stringResult);
        String purchaseRequestXml = stringResult.toString();
        Purchase purchaseRequest = new Purchase();
        purchaseRequest.setXml(purchaseRequestXml);
        com.travelinsurancemaster.clients.travelguard.PurchaseResponse response =
                (com.travelinsurancemaster.clients.travelguard.PurchaseResponse) getWebServiceTemplate()
                        .marshalSendAndReceive(purchaseRequest, new SoapActionCallback("https://tgpolicy.websvcs.travelguard.com/Purchase"));
        String purchaseResult = response.getPurchaseResult();
        StringReader stringReader = new StringReader(purchaseResult);
        Source source = new StreamSource(stringReader);
        return (PolicyPurchaseDetailsTG) jaxb2Marshaller.unmarshal(source);
    }

    @Override
    protected QuoteResult quoteInternal(QuoteRequest quoteRequest, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {
        String policyCode = policyMetaCode.getCode();
        log.debug("Product code: {}, product name: {}", policyCode, policyMetaCode.getName());
        PolicySpecificationTG policySpecification = new PolicySpecificationTG();
        PolicyTG policy = new PolicyTG();
        String[] planCodes = policyCode.split(CODE_SEPARATOR);
        ProductRequestTG product = new ProductRequestTG(planCodes[1], planCodes[0], QUOTE, MERCHANT_XML_QUOTE, getOptionalPackages(quoteRequest, policyCode, policyMeta.getId()));
        policy.setProduct(product);

        TravelersTG travelers = new TravelersTG();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        quoteRequest.getTravelers().forEach(genericTraveler -> {
            TravelerTG traveler = new TravelerTG(genericTraveler.getTripCost(), dateFormat.format(genericTraveler.getBirthdaySafe()));

            if(quoteRequest.getPlanType().getId() == PlanType.LIMITED.getId())
                traveler.setTripCost(BigDecimal.valueOf(0));

            travelers.getTravelers().add(traveler);
            traveler.setAddress(new AddressTG(quoteRequest.getResidentCountry(), quoteRequest.getResidentState()));
        });
        policy.setTravelers(travelers);
        DestinationsTG destinations = new DestinationsTG();
        destinations.getDestinations().add(new DestinationTG(quoteRequest.getDestinationCountry()));
        TripTG trip = new TripTG(dateFormat.format(quoteRequest.getDepartDate()), dateFormat.format(quoteRequest.getReturnDate()),
                quoteRequest.getDepositDate() != null ? dateFormat.format(quoteRequest.getDepositDate()) : dateFormat.format(LocalDate.now().minusDays(7)),
                quoteRequest.getPaymentDate() != null ? dateFormat.format(quoteRequest.getPaymentDate()) : dateFormat.format(LocalDate.now()),
                destinations);
        policy.setTrip(trip);
        policySpecification.setPolicy(policy);


        PolicyQuoteDetailsTG clientResponse = quote(policySpecification);


        QuoteResult quoteResult = new QuoteResult();
        if (clientResponse.getStatus().getErrors() != null && !clientResponse.getStatus().getErrors().getErrorsList().isEmpty()) {
            quoteResult.setStatus(Result.Status.ERROR);
            List<ErrorTG> errors = clientResponse.getStatus().getErrors().getErrorsList();
            errors.forEach(error -> quoteResult.getErrors().add(new Result.Error(error.getCode(), error.getDescription())));
        } else if (clientResponse.getStatus().getPackageWarnings() != null && !clientResponse.getStatus().getPackageWarnings().getPackageWarningsList().isEmpty()) {
            quoteResult.setStatus(Result.Status.ERROR);
            List<PackageWarningTG> packageWarnings = clientResponse.getStatus().getPackageWarnings().getPackageWarningsList();
            packageWarnings.forEach(packageWarning -> {
                List<Result.Error> errors = quoteResult.getErrors();
                if (CollectionUtils.isEmpty(errors)) {
                    errors.add(new Result.Error("-1", packageWarning.getDescription()));
                } else {
                    errors.stream().filter(error -> !error.getErrorMsg().equals(packageWarning.getDescription())).map(error -> new Result.Error("-1", packageWarning.getDescription())).forEachOrdered(errors::add);
                }
            });
        } else {
            quoteResult.setStatus(Result.Status.SUCCESS);
            quoteResult.transactionId = clientResponse.getStatus().getTransactionId();
            BigDecimal totalPremiumAmount = clientResponse.getPolicyInformation().getPremium().getTotalPremiumAmount();
            totalPremiumAmount = totalPremiumAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
            // generate dynamic certificate link
            Map<String, String> valuesMap = new HashMap<>();
            String productCode = policyCode.split(CODE_SEPARATOR)[0];
            valuesMap.put("stateCode", quoteRequest.getResidentState().name());
            valuesMap.put("productCode", productCode);
            valuesMap.put("planCode", (productCode.startsWith("NH") || productCode.startsWith("MT")) ? "P2" : "P1");
            StrSubstitutor sub = new StrSubstitutor(valuesMap);
            Product insurancePolicy = new Product(policyMeta, policyMetaCode, totalPremiumAmount, quoteRequest.getUpsaleValues(), sub.replace(CERTIFICATE_TEMPLATE));
            quoteResult.products.add(insurancePolicy);
        }
        return quoteResult;
    }

    private OptionalPackagesTG getOptionalPackages(QuoteRequest quoteRequest, String policyCode, Long policyMetaId) {
        OptionalPackagesTG optionalPackages = new OptionalPackagesTG();

        String rentalCar = quoteRequest.getUpsaleValue(CategoryCodes.RENTAL_CAR, DEFAULT_UPSALE_VALUE);
        if (StringUtils.isNoneBlank(rentalCar)) {
            OptionalPackageTG rentalCarPackage = new OptionalPackageTG();
            Integer rentalCarPackageId = optionalPackagesMap.get(policyCode + CategoryCodes.RENTAL_CAR);
            rentalCarPackage.setPackageId(rentalCarPackageId);
            rentalCarPackage.setDays((int) quoteRequest.getRentalCarLength());
            try {
                Double unitOfCoverage = Double.valueOf(rentalCar);
                rentalCarPackage.setUnitsOfCoverage(unitOfCoverage);
            } catch (NumberFormatException e) {
                // not a double, no unit of coverage
            }
            optionalPackages.getOptionalPackages().add(rentalCarPackage);
        }

        String cancelForAnyReasonValue = quoteRequest.getUpsaleValue(CategoryCodes.CANCEL_FOR_ANY_REASON, DEFAULT_UPSALE_VALUE);
        String cancelForAnyReasonApiValue = policyMetaCategoryValueService.getApiValue(policyMetaId, CategoryCodes.CANCEL_FOR_ANY_REASON, cancelForAnyReasonValue, quoteRequest);
        if (StringUtils.isNoneBlank(cancelForAnyReasonApiValue)) {
            OptionalPackageTG cancelForAnyReasonPackage = new OptionalPackageTG();
            Integer cancelForAnyReasonPackageId = optionalPackagesMap.get(policyCode + CategoryCodes.CANCEL_FOR_ANY_REASON);
            if (cancelForAnyReasonPackageId != null) {
                cancelForAnyReasonPackage.setPackageId(cancelForAnyReasonPackageId);
                double unitOfCoverage = Double.parseDouble(cancelForAnyReasonApiValue);
                cancelForAnyReasonPackage.setUnitsOfCoverage(unitOfCoverage);
                optionalPackages.getOptionalPackages().add(cancelForAnyReasonPackage);
            }
        }

        String cancelForWorkReason = quoteRequest.getUpsaleValue(CategoryCodes.CANCEL_FOR_WORK_REASONS, DEFAULT_UPSALE_VALUE);
        if (StringUtils.isNoneBlank(cancelForWorkReason)) {
            OptionalPackageTG cancelForWorkReasonPackage = new OptionalPackageTG();
            Integer cancelForWorkReasonPackageId = optionalPackagesMap.get(policyCode + CategoryCodes.CANCEL_FOR_WORK_REASONS);
            if (cancelForWorkReasonPackageId != null) {
                cancelForWorkReasonPackage.setPackageId(cancelForWorkReasonPackageId);
                optionalPackages.getOptionalPackages().add(cancelForWorkReasonPackage);
            }
        }

        String flightGuard = quoteRequest.getUpsaleValue(CategoryCodes.FLIGHT_ONLY_AD_AND_D, DEFAULT_UPSALE_VALUE);
        if (StringUtils.isNoneBlank(flightGuard)) {
            OptionalPackageTG flightGuardPackage = new OptionalPackageTG();
            Integer flightGuardPackageId = optionalPackagesMap.get(policyCode + CategoryCodes.FLIGHT_ONLY_AD_AND_D);
            if (flightGuardPackageId != null) {
                flightGuardPackage.setPackageId(flightGuardPackageId);
                flightGuardPackage.setUnitsOfCoverage(Double.valueOf(flightGuard));
                optionalPackages.getOptionalPackages().add(flightGuardPackage);
            }
        }

        String emergencyMedicalUpsaleValue = quoteRequest.getUpsaleValue(CategoryCodes.EMERGENCY_MEDICAL, DEFAULT_UPSALE_VALUE);
        String emergencyMedicalApiValue = policyMetaCategoryValueService.getApiValue(policyMetaId, CategoryCodes.EMERGENCY_MEDICAL, emergencyMedicalUpsaleValue, quoteRequest);
        if (StringUtils.isNoneBlank(emergencyMedicalApiValue)) {
            OptionalPackageTG emergencyMedicalPackage = new OptionalPackageTG();
            Integer emergencyMedicalPackageId = optionalPackagesMap.get(policyCode + CategoryCodes.EMERGENCY_MEDICAL);
            if (emergencyMedicalPackageId != null) {
                emergencyMedicalPackage.setPackageId(emergencyMedicalPackageId);
                optionalPackages.getOptionalPackages().add(emergencyMedicalPackage);
            }
        }

        String medicalEvacuationUpsaleValue = quoteRequest.getUpsaleValue(CategoryCodes.MEDICAL_EVACUATION);
        String medicalEvacuationApiValue = policyMetaCategoryValueService.getApiValue(policyMetaId, CategoryCodes.MEDICAL_EVACUATION, medicalEvacuationUpsaleValue, quoteRequest);
        if (StringUtils.isNotBlank(medicalEvacuationApiValue)) {
            OptionalPackageTG emergencyEvacuationPackage = new OptionalPackageTG();
            Integer emergencyEvacuationPackageId = optionalPackagesMap.get(policyCode + CategoryCodes.MEDICAL_EVACUATION);
            if (emergencyEvacuationPackageId != null) {
                emergencyEvacuationPackage.setPackageId(emergencyEvacuationPackageId);
                optionalPackages.getOptionalPackages().add(emergencyEvacuationPackage);
            }
        }

        return optionalPackages;
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest request) {
        QuoteRequest quoteRequest = request.getQuoteRequest();
        PolicyMeta policyMeta = request.getProduct().getPolicyMeta();
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCode(
                policyMeta.getId(), quoteRequest);
        PurchaseResponse result = new PurchaseResponse();
        if (policyMetaCode == null) {
            result.setStatus(Result.Status.ERROR);
            result.getErrors().add(new Result.Error("-1", "PolicyMetaCode is null!"));
        }
        PolicySpecificationTG policySpecification = new PolicySpecificationTG();
        PolicyTG policy = new PolicyTG();
        String policyCode = policyMetaCode.getCode();
        String[] planCodes = policyCode.split(CODE_SEPARATOR);
        ProductRequestTG product = new ProductRequestTG(planCodes[1], planCodes[0], PURCHASE, MERCHANT_XML_PURCHASE, getOptionalPackages(quoteRequest, policyCode, policyMeta.getId()));
        policy.setProduct(product);
        TravelersTG travelers = new TravelersTG();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        request.getTravelers().forEach(genericTraveler -> {
            TravelerNameTG travelerName = new TravelerNameTG(genericTraveler.getFirstName(), genericTraveler.getLastName());
            AddressTG address = new AddressTG(request.getQuoteRequest().getResidentCountry(), request.getQuoteRequest().getResidentState(), request.getCity(), request.getAddress(), request.getPostalCode());
            BeneficiariesTG beneficiaries = new BeneficiariesTG();
            beneficiaries.getBeneficiaries().add(new BeneficiaryTG(genericTraveler.getBeneficiary(), 100.));
            TravelerTG traveler = new TravelerTG(genericTraveler.getTripCost(), dateFormat.format(genericTraveler.getBirthdaySafe()), request.getEmail(), travelerName, address, beneficiaries);
            travelers.getTravelers().add(traveler);
        });
        policy.setTravelers(travelers);
        DestinationsTG destinations = new DestinationsTG();
        destinations.getDestinations().add(new DestinationTG(request.getQuoteRequest().getDestinationCountry()));
        TripTG trip = new TripTG(dateFormat.format(request.getQuoteRequest().getDepartDate()), dateFormat.format(request.getQuoteRequest().getReturnDate()),
                request.getQuoteRequest().getDepositDate() != null ? dateFormat.format(request.getQuoteRequest().getDepositDate()) : "",
                request.getQuoteRequest().getPaymentDate() != null ? dateFormat.format(request.getQuoteRequest().getPaymentDate()) : "",
                destinations);
        policy.setTrip(trip);
        String cardType;
        switch (request.getCreditCard().getCcType()) {
            case VISA:
                cardType = "VISA";
                break;
            case MasterCard:
                cardType = "MASTER CARD";
                break;
            case AmericanExpress:
                cardType = "AMERICAN EXPRESS";
                break;
            case Discover:
                cardType = "DISCOVER";
                break;
            case Diners:
                cardType = "DINERS CLUB";
                break;
            default:
                cardType = "";
        }

        CreditCardTG creditCard = new CreditCardTG(request.getProduct().getTotalPrice().doubleValue(), request.getCreditCard().getCcName(), request.getCreditCard().getCcNumber(),
                request.getCreditCard().getCcExpMonth(), request.getCreditCard().getCcExpYear(), cardType);
        policy.setPayment(new PaymentTG(request.getProduct().getTotalPrice().doubleValue(), new CreditCardDetailsTG(creditCard)));
        policy.setFulfillment(new FulfillmentTG(ONLINE, YES));
        policy.setAgency(new AgencyTG(apiProperties.getTravelGuard().getArc(), apiProperties.getTravelGuard().getAgentEmail()));

        policySpecification.setPolicy(policy);
        PolicyPurchaseDetailsTG clientResponse = purchase(policySpecification);
        if (clientResponse.getStatus().getErrors() != null && !clientResponse.getStatus().getErrors().getErrorsList().isEmpty()) {
            result.setStatus(Result.Status.ERROR);
            List<ErrorTG> errors = clientResponse.getStatus().getErrors().getErrorsList();
            errors.forEach(error -> result.getErrors().add(new Result.Error(error.getCode(), error.getDescription())));
        } else {
            result.setStatus(Result.Status.SUCCESS);
            result.setPolicyNumber(clientResponse.getPolicyInformation().getPolicyNumber());
        }
        return result;
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.TravelGuard;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        return Collections.emptyList();
    }
}
