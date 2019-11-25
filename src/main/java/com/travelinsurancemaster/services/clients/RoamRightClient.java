package com.travelinsurancemaster.services.clients;

import com.travelinsurancemaster.model.ApiVendor;
import com.travelinsurancemaster.model.CategoryCodes;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.CreditCard;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.webservice.common.*;
import com.travelinsurancemaster.services.AbstractInsuranceVendorApi;
import com.travelinsurancemaster.services.PolicyMetaCodeService;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.QuoteStorageService;
import com.travelinsurancemaster.util.DateUtil;
import com.travelinsurancemaster.util.NumberUtils;
import com.travelinsurancemaster.util.TextUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by raman on 02.08.19.
 */
public class RoamRightClient extends AbstractInsuranceVendorApi {

    private static final String ESSENTIAL_CODE = "RREssential";
    private static final String PRO_CODE = "RRPro";
    private static final String PRO_PLUS_CODE = "RRProPlus";

    private static final int MAX_NUMBER_OF_TRAVELERS = 16;

    private static final List<String> policyMetaCodes = Arrays.asList(ESSENTIAL_CODE, PRO_CODE, PRO_PLUS_CODE);

    @Autowired
    PolicyMetaService policyMetaService;

    @Autowired
    QuoteStorageService quoteStorageService;

    //policy code -> submit numerical end of id part
    private static final Map<String, String> policyCodeToSubmitElementIds = Stream.of(
            new AbstractMap.SimpleEntry<String, String>(ESSENTIAL_CODE, "347"),
            new AbstractMap.SimpleEntry<String, String>(PRO_CODE, "526"),
            new AbstractMap.SimpleEntry<String, String>(PRO_PLUS_CODE, "527")
        )
        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

    //policy code -> [ category Code -> input field id ]
    private static final Map<String, Map<String, String>> policyCodeToCategoryCodeToElementIds = Stream.of(
            new AbstractMap.SimpleEntry<String, Map<String, String>>(
                ESSENTIAL_CODE,
                Stream.of(
                    new AbstractMap.SimpleEntry<String, String>(CategoryCodes.RENTAL_CAR, "upgrade_347_6333"),
                    new AbstractMap.SimpleEntry<String, String>(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "upgrade_347_6332"),
                    new AbstractMap.SimpleEntry<String, String>(CategoryCodes.EMERGENCY_MEDICAL, "upgrade_347_6331")
                ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
            ),
            new AbstractMap.SimpleEntry<String, Map<String, String>>(
                    PRO_CODE,
                    Stream.of(
                            new AbstractMap.SimpleEntry<String, String>(CategoryCodes.CANCEL_FOR_ANY_REASON, "upgrade_526_9662"),
                            new AbstractMap.SimpleEntry<String, String>(CategoryCodes.BAGGAGE_LOSS, "upgrade_526_9664"),
                            new AbstractMap.SimpleEntry<String, String>(CategoryCodes.HAZARDOUS_SPORTS, "upgrade_526_9673"),
                            new AbstractMap.SimpleEntry<String, String>(CategoryCodes.RENTAL_CAR, "upgrade_526_9663"),
                            new AbstractMap.SimpleEntry<String, String>(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "upgrade_526_9674")
                    ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
            ),
            new AbstractMap.SimpleEntry<String, Map<String, String>>(
                    PRO_PLUS_CODE,
                    Stream.of(
                            new AbstractMap.SimpleEntry<String, String>(CategoryCodes.CANCEL_FOR_ANY_REASON, "upgrade_527_9687"),
                            new AbstractMap.SimpleEntry<String, String>(CategoryCodes.BAGGAGE_LOSS, "upgrade_527_9689"),
                            new AbstractMap.SimpleEntry<String, String>(CategoryCodes.HAZARDOUS_SPORTS, "upgrade_527_9698"),
                            new AbstractMap.SimpleEntry<String, String>(CategoryCodes.RENTAL_CAR, "upgrade_527_9688"),
                            new AbstractMap.SimpleEntry<String, String>(CategoryCodes.FLIGHT_ONLY_AD_AND_D, "upgrade_527_9699")
                    ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
            )
    ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

    @Autowired
    PolicyMetaCodeService policyMetaCodeService;

    @Override
    protected QuoteResult quoteInternal(QuoteRequest quoteRequest, PolicyMeta policyMeta, PolicyMetaCode policyMetaCode) {

        QuoteResult quoteResult = new QuoteResult();

        String quoteRequestId = quoteRequest.getQuoteId();

        QuoteStorage quoteStorage = quoteStorageService.findOne(quoteRequestId);
        QuoteRequest storageQuoteRequest = quoteStorage.getQuoteRequestObj();

        Map<String, Map<String, BigDecimal>> basePrices = storageQuoteRequest.getBasePrices();

        Map<String, Map<String, Map<String, Map<String, BigDecimal>>>> categoryPrices = storageQuoteRequest.getCategoryPrices();

        //If price info about the quote exists, use it to compute the quote
        if(basePrices != null) {
            Map<String, BigDecimal> vendorBasePrices = basePrices.get(policyMeta.getVendor().getCode());
            if(vendorBasePrices != null) {
                BigDecimal basePrice = vendorBasePrices.get(policyMetaCode.getCode());
                if(basePrice != null) {
                    return doQuoteFromQuoteRequest(quoteResult, quoteRequest, basePrice, categoryPrices, policyMeta, policyMetaCode);
                }
            }
        }

        //If price info is absent, retrieve it from the vendor

        if(apiProperties.getWebDriverPath() == null) {
            quoteResult.setStatus(Result.Status.ERROR);
            quoteResult.getErrors().add(new Result.Error("-1", "WebDriver path is null!"));
            System.err.println("WebDriver path is null!");
            return quoteResult;
        }

        WebHandler webHandler = new WebHandler(apiProperties.getWebDriverPath(), apiProperties.getRoamRight().getUrl());

        System.out.println("webDriver=" + webHandler.getWebDriver());
        //WebHandler webHandler = new WebHandler(apiProperties.getWebDriverPath(), apiProperties.getRoamRight().getUrl());

        quoteResult = doQuote(webHandler, quoteRequest);
        if(quoteResult.getStatus() == Result.Status.ERROR) {
            return quoteResult;
        }

        WebDriver webDriver = webHandler.getWebDriver();
        WebDriverWait webDriverWait = webHandler.getWebDriverWait();
        JavascriptExecutor je = webHandler.getJe();

        int policyIndex = policyMetaCodes.indexOf(policyMetaCode.getCode());
        if(policyIndex < 0) {
            return doQuitWebDriverAndPrepareErrorResult(webDriver, quoteResult, "Policy meta code " + policyMetaCode.getCode() + " is not supported");
        }

        BigDecimal basePrice = getTotalPrice(webDriver, policyIndex);

        if(basePrice == null) {
            return doQuitWebDriverAndPrepareErrorResult(webDriver, quoteResult, "Couldn't find the price field on the select plan form");
        }

        Map<String, String> categoryCodeToElementIds = policyCodeToCategoryCodeToElementIds.get(policyMetaCode.getCode());

        List<PolicyMetaCategory> upsaleCategories = policyMetaService.getUpsalesFromPolicy(policyMeta, quoteRequest);
        Map<String, Map<String, BigDecimal>> policyCategoryPrices = new HashMap<>();
        for(PolicyMetaCategory upsaleCategory : upsaleCategories) {
            String elementId = categoryCodeToElementIds.get(upsaleCategory.getCategory().getCode());
            if(elementId != null) {
                String categoryCode = upsaleCategory.getCategory().getCode();
                WebElement element = webDriver.findElement(By.id(elementId));
                String upsaleText = (String) je.executeScript("return arguments[0].parentNode.parentNode.innerText", element);
                BigDecimal upsalePrice = parsePrice(upsaleText);
                List<PolicyMetaCategoryValue> upsaleCategoryValues = upsaleCategory.getValues();
                Optional<PolicyMetaCategoryValue> nonZeroValueOptional = upsaleCategoryValues.stream().filter(v -> Integer.parseInt(v.getValue()) > 0).findFirst();
                if(nonZeroValueOptional.isPresent()) {
                    Map<String, BigDecimal> categoryValuePrices = policyCategoryPrices.get(categoryCode);
                    if(categoryValuePrices == null) categoryValuePrices = new HashMap<>();
                    categoryValuePrices.put(nonZeroValueOptional.get().getValue(), upsalePrice);
                    policyCategoryPrices.put(categoryCode, categoryValuePrices);
                }
            }
        }

        quoteStorageService.insertVendorPolicyBasePrice(quoteRequestId, policyMeta, policyMetaCode, basePrice);
        quoteStorageService.insertVendorPolicyCategoryPrices(quoteRequestId, policyMeta, policyMetaCode, policyCategoryPrices);
        quoteStorage = quoteStorageService.findOne(quoteRequestId);
        storageQuoteRequest = quoteStorage.getQuoteRequestObj();

        webDriver.quit();

        return doQuoteFromQuoteRequest(quoteResult, quoteRequest, basePrice, storageQuoteRequest.getCategoryPrices(), policyMeta, policyMetaCode);
    }

    @Override
    protected PurchaseResponse purchaseInternal(PurchaseRequest purchaseRequest) {

        PurchaseResponse purchaseResponse = new PurchaseResponse();
        QuoteRequest quoteRequest = purchaseRequest.getQuoteRequest();

        WebHandler webHandler = new WebHandler(apiProperties.getWebDriverPath(), apiProperties.getRoamRight().getUrl());

        //Do quote request as the start of the purchase
        QuoteResult quoteResult = doQuote(webHandler, quoteRequest);
        if(quoteResult.getStatus() == Result.Status.ERROR) {
            purchaseResponse.setStatus(Result.Status.ERROR);
            purchaseResponse.getErrors().add(new Result.Error("-1", quoteResult.getErrorMsg()));
            return purchaseResponse;
        }

        WebDriver webDriver = webHandler.getWebDriver();
        WebDriverWait webDriverWait = webHandler.getWebDriverWait();
        JavascriptExecutor je = webHandler.getJe();

        //Fill upsales
        String policyMetaCode = purchaseRequest.getProduct().getPolicyMetaCode().getCode();
        Map<String, String> categoryCodeToElementIds = policyCodeToCategoryCodeToElementIds.get(policyMetaCode);

        for(String category : quoteRequest.getCategories().keySet()) {
            String elementId = categoryCodeToElementIds.get(category);
            if(elementId != null) {
                WebElement element = webDriver.findElement(By.id(categoryCodeToElementIds.get(category)));
                click(je, element);
            }
        }

        //Submit select plan page
        WebElement buyButton = webDriver.findElement(By.cssSelector("[name$=btnplanBenefitUpgradePrice_Buy_" + policyCodeToSubmitElementIds.get(policyMetaCode) + "]"));
        click(je, buyButton);

        //Wait for travel details page
        WebElement travelerDetailsDiv = null;
        try {
            travelerDetailsDiv = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("TravelerDetails")));
        } catch (StaleElementReferenceException e) {}

        if(travelerDetailsDiv == null) {
            return doQuitWebDriverAndPrepareErrorResult(webDriver, purchaseResponse, "Couldn't find the travel details div on the travel details form");
        }

        //Fill destination
        setSelectValueByVisibleText(webDriver, "[name$=ddlDestinations]", quoteRequest.getDestinationCountry().getCaption());

        //Fill travelers
        int num = 0;
        for(GenericTraveler traveler : purchaseRequest.getTravelers()) {
            setValue(webDriver, je, "[id$=TravelerDetails_FirstName_" + num + "]", traveler.getFirstName());
            setValue(webDriver, je, "[id$=TravelerDetails_LastName_" + num + "]", traveler.getLastName());
            String birthday = traveler.getBirthdaySafeStr();
            setValue(webDriver, je, "[id$=TravelerDetails_DOB_" + num + "]", birthday);
            setValue(webDriver, je, "[id$=TravelerDetails_OrigDOB_" + num + "]", birthday);
            num++;
        }

        //Fill primary traveler address
        setValue(webDriver, je, "[id$=AddressEmailPhone_Address1]", purchaseRequest.getAddress());
        setValue(webDriver, je, "[id$=AddressEmailPhone_Address2]", purchaseRequest.getAddressLine2());
        setValue(webDriver, je, "[id$=AddressEmailPhone_City]", purchaseRequest.getCity());
        setSelectValueByVisibleText(webDriver, "[id$=AddressEmailPhone_stateDDL]", quoteRequest.getResidentState().name());
        setValue(webDriver, je, "[id$=AddressEmailPhone_ZIPCode]", purchaseRequest.getPostalCode());
        setSelectValueByVisibleText(webDriver, "[id$=AddressEmailPhone_countryDDL]", quoteRequest.getResidentCountry().getCaption());
        setValue(webDriver, je, "[id$=AddressEmailPhone_Phone]", purchaseRequest.getPhone());
        setValue(webDriver, je, "[id$=AddressEmailPhone_Email]", purchaseRequest.getEmail());

        //Submit traveler details page
        buyButton = webDriver.findElement(By.cssSelector("[name$=btnBuy"));
        click(je, buyButton);

        //Wait for payment details page
        WebElement billingDetailsDiv = null;
        try {
            billingDetailsDiv = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingDetails")));
        } catch (StaleElementReferenceException e) {}

        if(billingDetailsDiv == null) {
            return doQuitWebDriverAndPrepareErrorResult(webDriver, purchaseResponse, "Couldn't find the billing details div on the travel purchase policy form");
        }

        //Fill payment information
        CreditCard creditCard = purchaseRequest.getCreditCard();
        setValue(webDriver, je, "[id$=BillingInfo_CardNumber]", String.valueOf(creditCard.getCcNumber()));
        setValue(webDriver, je, "[id$=BillingInfo_CCVNumber]", creditCard.getCcCode());
        setValue(webDriver, je, "[id$=BillingInfo_CCVNumber]", creditCard.getCcCode());
        setSelectValueByVisibleText(webDriver, "[id$=BillingInfo_ExpMonth]", creditCard.getCcExpMonth());
        setSelectValueByVisibleText(webDriver, "[id$=BillingInfo_ExpYear]", creditCard.getCcExpYear());

        //Check agreement checkbox
        WebElement agreement =  webDriver.findElement(By.cssSelector("[id$=BillingInfo_AgreeToTerms"));
        agreement.click();

        //Submit traveler details page
        buyButton = webDriver.findElement(By.cssSelector("[id$=btnBuy"));
        click(je, buyButton);

        //Wait for payment result page or error page
        try {//BillingInfo_valSummary
            webDriverWait.until(
                    ExpectedConditions.or(
                            ExpectedConditions.invisibilityOfElementLocated(By.id("BillingDetails")),
                            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[id$=BillingInfo_valSummary]"))
                    )
            );
        } catch (StaleElementReferenceException e) {}

        WebElement validationSummary = webDriver.findElement(By.cssSelector("[id$=BillingInfo_valSummary]"));
        if(validationSummary != null) {
            String error = validationSummary.findElements(By.tagName("li")).stream().map(e -> e.getText()).collect(Collectors.joining(" "));
            return doQuitWebDriverAndPrepareErrorResult(webDriver, purchaseResponse, error);
        }

        //Clean style and javascript from the result page
        Document doc = Jsoup.parse(webDriver.getPageSource());
        doc.select("script, style").remove();
        Elements all = doc.select("*");
        for(Element el : all) {
            for(Attribute attr : el.attributes()) {
                String attrKey = attr.getKey();
                if(attrKey.startsWith("on")) {
                    el.removeAttr(attrKey);
                }
            }
        }

        purchaseResponse.setResultPage(doc.outerHtml());

        purchaseResponse.setStatus(Result.Status.SUCCESS);
        purchaseResponse.setPolicyNumber("-1");

        webDriver.quit();

        return purchaseResponse;
    }

    private QuoteResult doQuote(WebHandler webHandler, QuoteRequest quoteRequest) {

        QuoteResult quoteResult = new QuoteResult();

        WebDriver webDriver = webHandler.getWebDriver();
        WebDriverWait webDriverWait = webHandler.getWebDriverWait();
        JavascriptExecutor je = webHandler.getJe();

        WebElement quoteFormSubmit = null;

        List<WebElement> elements = webDriver.findElements(By.cssSelector("[name^=ct]"));

        //Fill quote parameters
        try {
            for(WebElement element : elements) {
                switch(getInputName(element)) {
                    case "residenceDDL":
                        Select select = new Select(element);
                        if(quoteRequest.getResidentCountry().equals(CountryCode.US)) {
                            select.selectByVisibleText(quoteRequest.getResidentState().getCaption());
                        } else {
                            select.selectByVisibleText(TextUtils.capitalizeEachWord(quoteRequest.getResidentCountry().getCaption().toLowerCase()));
                        }
                        break;
                    case "DepartureDate":
                        setAttribute(je, element, "value", DateUtil.getLocalDateStr(quoteRequest.getDepartDate()));
                        break;
                    case "ReturnDate":
                        setAttribute(je, element, "value", DateUtil.getLocalDateStr(quoteRequest.getReturnDate()));
                        break;
                    case "TripDepositDate":
                        setAttribute(je, element, "value", DateUtil.getLocalDateStr(quoteRequest.getDepositDate()));
                        break;
                    case "TripCostType":
                        if(element.getAttribute("value").equals("0")) {
                            click(je, element);
                        }
                        break;
                    case "tripCost":
                        setAttribute(je, element, "value", String.valueOf(quoteRequest.getTripCost().intValue()));
                        break;
                    case "formSubmit":
                        quoteFormSubmit = element;
                        break;
                }
            }
        } catch (StaleElementReferenceException e) {
            WebElement contentDiv = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("content")));
            String error = e.getMessage();
            if(contentDiv != null) {
                WebElement errorTitle = contentDiv.findElement(By.tagName("h1"));
                if(errorTitle != null) {
                    error = errorTitle.getText();
                }
            }
            return doQuitWebDriverAndPrepareErrorResult(webDriver, quoteResult, error);
        }

        int travelerNum = quoteRequest.getTravelers().size();
        if(travelerNum > MAX_NUMBER_OF_TRAVELERS) {
            return doQuitWebDriverAndPrepareErrorResult(webDriver, quoteResult, "Number of travelers can't exceed " + MAX_NUMBER_OF_TRAVELERS);
        }

        //Fill traveler ages
        int num = 1;
        for(GenericTraveler traveler : quoteRequest.getTravelers()) {
            WebElement element = webDriver.findElement(By.cssSelector("[name$=TotalAge_" + String.format("%02d", num) + "]"));
            setAttribute(je, element, "value", String.valueOf(traveler.getAge()));
            num++;
        }

        if(quoteFormSubmit == null) {
            return doQuitWebDriverAndPrepareErrorResult(webDriver, quoteResult, "Couldn't find the submit button on the quote form");
        }

        quoteFormSubmit.click();

        WebElement resultSpan = null;
        try {
            resultSpan = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cphMain_cphInternal_dlPlanSummary")));
        } catch (StaleElementReferenceException e) {}

        if(resultSpan == null) {
            return doQuitWebDriverAndPrepareErrorResult(webDriver, quoteResult, "Couldn't find the result span on the quote result form");
        }

        return quoteResult;
    }

    private void setAttribute(JavascriptExecutor je, WebElement element, String name, String value) {
        je.executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", element, name, value);
    }

    private void click(JavascriptExecutor je, WebElement element) {
        je.executeScript("arguments[0].click();", element);
    }

    private void setValue(WebDriver webDriver, JavascriptExecutor je, String cssSelector, String value) {
        WebElement element = webDriver.findElement(By.cssSelector(cssSelector));
        if(element != null) {
            setAttribute(je, element, "value", value);
        }
    }

    private void setSelectValueByVisibleText(WebDriver webDriver, String cssSelector, String visibleTextValue) {
        WebElement destination = webDriver.findElement(By.cssSelector(cssSelector));
        Select destinationSelect = new Select(destination);
        destinationSelect.selectByVisibleText(visibleTextValue);
    }

    private String getInputName(WebElement input) {
        String name = input.getAttribute("name");
        String inputName = null;
        if(name != null && StringUtils.isNotBlank(name)) {
            int datePickerPos = name.indexOf("$DatePickerDate");
            if (datePickerPos >= 0) {
                name = name.substring(0, datePickerPos);
            }
            if(StringUtils.isNotBlank(name)) {
                inputName = name.substring(name.lastIndexOf("$") + 1);
            }
        }
        return inputName;
    }

    private QuoteResult doQuitWebDriverAndPrepareErrorResult(WebDriver webDriver, QuoteResult quoteResult, String error) {
        quoteResult.setStatus(Result.Status.ERROR);
        quoteResult.getErrors().add(new Result.Error("-1", error));
        webDriver.quit();
        return quoteResult;
    }

    private PurchaseResponse doQuitWebDriverAndPrepareErrorResult(WebDriver webDriver, PurchaseResponse purchaseResponse, String error) {
        purchaseResponse.setStatus(Result.Status.ERROR);
        purchaseResponse.getErrors().add(new Result.Error("-1", error));
        webDriver.quit();
        return purchaseResponse;
    }

    private QuoteResult doQuoteFromQuoteRequest(
            QuoteResult quoteResult, QuoteRequest quoteRequest,
            BigDecimal basePrice,
            Map<String, Map<String, Map<String, Map<String, BigDecimal>>>> categoryPrices,
            PolicyMeta policyMeta, PolicyMetaCode policyMetaCode
    ) {
        BigDecimal totalPrice = basePrice != null ? basePrice : BigDecimal.ZERO;
        Map<String, Map<String, Map<String, BigDecimal>>> vendorCategoryPrices = categoryPrices.get(policyMeta.getVendor().getCode());
        Map<String, String> upsaleValueMap = new HashMap<>();
        if(vendorCategoryPrices != null) {
            Map<String, Map<String, BigDecimal>> policyCategoryPrices = vendorCategoryPrices.get(policyMetaCode.getCode());
            if(policyCategoryPrices != null) {
                //Get categories from quote request
                Map<String, String> categories = quoteRequest.getCategories();
                //Loop over categories from quote request
                for(String category : categories.keySet()) {
                    //Get category value prices for each category
                    Map<String, BigDecimal> categoryValuePrices = policyCategoryPrices.get(category);
                    if(categoryValuePrices != null) {
                        //Get category value for the current category from quote request
                        String categoryValue = categories.get(category);
                        if(categoryValue != null) {
                            BigDecimal categoryValuePrice = null;
                            if(org.apache.commons.lang3.math.NumberUtils.isParsable(categoryValue)) {
                                //If category value is a parsable numeric, use this as the key to get the price from the category value price map
                                categoryValuePrice = categoryValuePrices.get(categoryValue);
                            } else {
                                //If category value is not a parsable numeric, use this the first key to get the price from the category value price map
                                Optional<String> categoryValuePriceKeyOptional = categoryValuePrices.keySet().stream().findFirst();
                                if (categoryValuePriceKeyOptional.isPresent()) {
                                    String categoryValuePriceKey = categoryValuePriceKeyOptional.get();
                                    categoryValuePrice = categoryValuePrices.get(categoryValuePriceKey);
                                }
                            }
                            if(categoryValuePrice != null) {
                                totalPrice = totalPrice.add(categoryValuePrice);
                                upsaleValueMap.put(category, String.valueOf(categoryValuePrice));
                            }
                        }
                    }
                }
            }
        }
        Product product = new Product(policyMeta, policyMetaCode, totalPrice, upsaleValueMap);
        quoteResult.products.add(product);
        quoteResult.setStatus(Result.Status.SUCCESS);
        return quoteResult;
    }

    private BigDecimal getTotalPrice(WebDriver webDriver, int policyIndex) {

        BigDecimal totalPrice = null;

        String cssClass = NumberUtils.getOrdinal(policyIndex + 2);
        List<WebElement> planSummaries = webDriver.findElements(By.className("planSummary"));
        for(WebElement planSummary : planSummaries) {
            String planSummaryClass = planSummary.getAttribute("class");
            if(planSummaryClass.contains(cssClass)) {
                totalPrice = NumberUtils.parseBigDecimal(planSummary.findElement(By.id("planSummaryPrice")).getText().substring(1));
                break;
            }
        }

        return totalPrice;
    }

    private BigDecimal parsePrice(String text) {
        BigDecimal price = null;
        if(text != null) {
            //Parse upsale price
            int priceStartIndex = text.indexOf("($");
            if(priceStartIndex >= 0) {
                text = text.substring(priceStartIndex + 2);
                int priceEndIndex = text.indexOf(")");
                if(priceEndIndex >= 0) {
                    text = text.substring(0, priceEndIndex - 1);
                    price = new BigDecimal(text);
                }
            }
        }
        return price;
    }

    @Override
    public void filterPolicyUpsaleCategories(List<PolicyMetaCategory> upsaleCategories, Product product, QuoteRequest quoteRequest) {
        PolicyMetaCode policyMetaCode = product.getPolicyMetaCode();
        if(policyMetaCode != null) {
            Map<String, String> categoryCodeToElementIds = policyCodeToCategoryCodeToElementIds.get(policyMetaCode.getCode());
            if(categoryCodeToElementIds != null) {
                Set<String> categoryCodes = categoryCodeToElementIds.keySet();
                upsaleCategories.removeIf(upsaleCategory -> !categoryCodes.contains(upsaleCategory.getCategory().getCode()));
            }
        }
    }

    @Override
    public String getVendorCode() {
        return ApiVendor.RoamRight;
    }

    @Override
    protected List<Result.Error> validateQuoteRequest(QuoteRequest request, PolicyMetaCode policyMetaCode) {
        return Collections.emptyList();
    }
}