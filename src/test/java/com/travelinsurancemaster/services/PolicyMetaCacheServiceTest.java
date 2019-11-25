package com.travelinsurancemaster.services;

import com.travelinsurancemaster.InsuranceMasterApp;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaCategoryContent;
import com.travelinsurancemaster.model.dto.cms.page.PolicyMetaPage;
import com.travelinsurancemaster.services.cms.PolicyMetaCategoryContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@ActiveProfiles({"test"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InsuranceMasterApp.class)
@Transactional(propagation = Propagation.NEVER)
public class PolicyMetaCacheServiceTest {

    private final String FAIL_UPDATE_MESSAGE = "Policy meta cache has not been updated!";
    private final String TEST_SUFFIX = "_TEST";
    private final int TEST_NUM_ADD = 1;

    private final long POLICY_META_ID = 1;
    private final long POLICY_META_FOR_PAGE_ID = 1568;
    private final long POLICY_META_RESTRICTION_ID = 121;
    private final long POLICY_META_RESTRICTION_COUNTRY_AND_STATE_ID = 122;

    private final long POLICY_META_FOR_CATEGORY_ID = 25;
    private final long POLICY_META_CATEGORY_ID = 339;
    private final long POLICY_META_CATEGORY_RESTRICTION_ID = 1;
    private final long POLICY_META_CATEGORY_RESTRICTION_COUNTRY_AND_STATE_ID = 2;
    private final long POLICY_META_CATEGORY_CONTENT_ID = 1;

    private final long POLICY_META_CODE_ID = 1;
    private final long POLICY_META_CODE_RESTRICTION_ID = 1;
    private final long POLICY_META_CODE_RESTRICTION_COUNTRY_AND_STATE_ID = 2;

    private final long POLICY_META_FOR_PACKAGE_ID = 1;
    private final long POLICY_META_PACKAGE_ID = 1;
    private final long POLICY_META_PACKAGE_RESTRICTION_ID = 1;
    private final long POLICY_META_PACKAGE_RESTRICTION_COUNTRY_AND_STATE_ID = 2;

    private final long POLICY_META_FOR_QUOTE_PARAM_ID = 1;
    private final long POLICY_QUOTE_PARAM_ID = 1;
    private final long POLICY_QUOTE_PARAM_RESTRICTION_ID = 1;
    private final long POLICY_QUOTE_PARAM_RESTRICTION_COUNTRY_AND_STATE_ID = 2;

    private final long VENDOR_ID = 1;

    @Autowired
    private PolicyMetaCacheService policyMetaCacheService;

    @Autowired
    private PolicyMetaService policyMetaService;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private PolicyMetaCategoryService policyMetaCategoryService;

    @Autowired
    private PolicyMetaCategoryContentService policyMetaCategoryContentService;

    @Autowired
    private SubcategoryValueService subcategoryValueService;

    @Autowired
    private PolicyMetaCodeService policyMetaCodeService;

    @Autowired
    private PolicyMetaPackageService policyMetaPackageService;

    @Autowired
    private PolicyQuoteParamService policyQuoteParamService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private VendorService vendorService;

    //*****************************************************************************************************************
    //**************************************POLICY META TESTS**********************************************************
    //*****************************************************************************************************************
    @Test
    public void policyMetaTest() {
        //Change a policy meta field and get cache updated
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(POLICY_META_ID);
        policyMeta.setDisplayName(policyMeta.getDisplayName() + TEST_SUFFIX);
        policyMetaService.save(policyMeta);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());

        //Change a policy meta percent info field and get cache updated
        PercentInfo percentInfo = policyMeta.getPercentInfo().get(0);
        percentInfo.setValue(percentInfo.getValue() + TEST_NUM_ADD);
        policyMetaService.save(policyMeta);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    @Test
    public void policyMetaPageTest() {
        //Change a policy meta page field and get cache updated
        PolicyMeta policyMeta = policyMetaService.getPolicyMetaById(POLICY_META_FOR_PAGE_ID);
        PolicyMetaPage page = policyMeta.getPolicyMetaPage();
        page.setName(page.getName() + TEST_SUFFIX);
        policyMetaService.save(policyMeta);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    @Test
    public void policyMetaRestrictionTest() {
        //Change a policy meta field and get cache updated
        PolicyMetaRestriction restriction = restrictionService.getPolicyMetaRestrictionById(POLICY_META_RESTRICTION_ID);
        restriction.setMaxValue(restriction.getMaxValue() + TEST_NUM_ADD);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    @Test
    public void policyMetaRestrictionCountryAndStateTest() {

        PolicyMetaRestriction restriction = restrictionService.getPolicyMetaRestrictionById(POLICY_META_RESTRICTION_COUNTRY_AND_STATE_ID);

        //Change the country list and get cache updated
        restriction.getCountries().add(CountryCode.US);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());

        //Change the state list and get cache updated
        restriction.getStates().add(StateCode.CA);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    //*****************************************************************************************************************
    //**************************************POLICY META CATEGORY TESTS*************************************************
    //*****************************************************************************************************************
    @Test
    public void policyMetaCategoryTest() {
        //Change a category field and get cache updated
        PolicyMetaCategory category = getPolicyMetaCategory(POLICY_META_FOR_CATEGORY_ID, POLICY_META_CATEGORY_ID);

        category.setDescription(category.getDescription() + TEST_SUFFIX);
        policyMetaCategoryService.save(category);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());

        //Change a category value and get cache updated
        PolicyMetaCategoryValue value =  category.getValues().get(0);
        value.setValue(value.getValue() + TEST_SUFFIX);
        policyMetaCategoryService.save(category);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());

        //Change a subcategory value and get cache updated
        SubcategoryValue subcategoryValue = value.getSubcategoryValuesList().get(0);
        subcategoryValue.setSubcategoryValue(subcategoryValue.getSubcategoryValue() + TEST_SUFFIX);
        subcategoryValueService.save(subcategoryValue);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    private PolicyMetaCategory getPolicyMetaCategory(long policyMetaId, long categoryId) {
        return policyMetaService.getPolicyMetaById(policyMetaId).getPolicyMetaCategories().stream().filter(c -> c.getId() == categoryId).findFirst().get();
    }

    @Test
    public void policyMetaCategoryRestrictionTest() {
        //Change a policy meta field and get cache updated
        PolicyMetaCategoryRestriction restriction = restrictionService.getPolicyMetaCategoryRestrictionById(POLICY_META_CATEGORY_RESTRICTION_ID);
        restriction.setMaxValue(restriction.getMaxValue() + TEST_NUM_ADD);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    @Test
    public void policyMetaCategoryRestrictionCountryAndStateTest() {

        PolicyMetaCategoryRestriction restriction = restrictionService.getPolicyMetaCategoryRestrictionById(POLICY_META_CATEGORY_RESTRICTION_COUNTRY_AND_STATE_ID);

        //Change the country list and get cache updated
        restriction.getCountries().add(CountryCode.US);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());

        //Change the state list and get cache updated
        restriction.getStates().add(StateCode.CA);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    @Test
    public void policyMetaCategoryContentTest() {

        PolicyMetaCategoryContent content = policyMetaCategoryContentService.getPolicyMetaCategoryContent(POLICY_META_CATEGORY_CONTENT_ID);

        //Change the content and get cache updated
        content.setContent(content.getContent() + TEST_SUFFIX);
        policyMetaCategoryContentService.save(content);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    //*****************************************************************************************************************
    //**************************************POLICY META CODE TESTS*****************************************************
    //*****************************************************************************************************************
    @Test
    public void policyMetaCodeTest() {
        //Change a policy meta code field and get cache updated
        PolicyMetaCode policyMetaCode = policyMetaCodeService.getPolicyMetaCodeById(POLICY_META_CODE_ID);
        policyMetaCode.setCode(policyMetaCode.getCode() + TEST_SUFFIX);
        policyMetaCodeService.save(policyMetaCode);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    @Test
    public void policyMetaCodeRestrictionTest() {
        //Change a policy meta field and get cache updated
        PolicyMetaCodeRestriction restriction = restrictionService.getPolicyMetaCodeRestrictionById(POLICY_META_CODE_RESTRICTION_ID);
        restriction.setMaxValue(restriction.getMaxValue() + TEST_NUM_ADD);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    @Test
    public void policyMetaCodeRestrictionCountryAndStateTest() {

        PolicyMetaCodeRestriction restriction = restrictionService.getPolicyMetaCodeRestrictionById(POLICY_META_CODE_RESTRICTION_COUNTRY_AND_STATE_ID);

        //Change the country list and get cache updated
        restriction.getCountries().add(CountryCode.US);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());

        //Change the state list and get cache updated
        restriction.getStates().add(StateCode.CA);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    //*****************************************************************************************************************
    //**************************************POLICY META PACKAGE TESTS**************************************************
    //*****************************************************************************************************************
    @Test
    public void policyMetaPackageTest() {
        //Change a policy meta code field and get cache updated
        PolicyMetaPackage policyMetaPackage = getPolicyMetaPackage(POLICY_META_FOR_PACKAGE_ID, POLICY_META_PACKAGE_ID);
        policyMetaPackage.setCode(policyMetaPackage.getCode() + TEST_SUFFIX);
        policyMetaPackageService.save(policyMetaPackage);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());

        //Change a package value and get cache updated
        PolicyMetaPackageValue value =  policyMetaPackage.getPolicyMetaPackageValues().get(0);
        value.setValue(value.getValue() + TEST_SUFFIX);
        policyMetaPackageService.save(policyMetaPackage);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    private PolicyMetaPackage getPolicyMetaPackage(long policyMetaId, long packageId) {
        return policyMetaService.getPolicyMetaById(policyMetaId).getPolicyMetaPackages().stream().filter(c -> c.getId() == packageId).findFirst().get();
    }

    @Test
    public void policyMetaPackageRestrictionTest() {
        //Change a policy meta field and get cache updated
        PolicyMetaPackageRestriction restriction = restrictionService.getPolicyMetaPackageRestrictionById(POLICY_META_PACKAGE_RESTRICTION_ID);
        restriction.setMaxValue(restriction.getMaxValue() + TEST_NUM_ADD);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    @Test
    public void policyMetaPackageRestrictionCountryAndStateTest() {

        PolicyMetaPackageRestriction restriction = restrictionService.getPolicyMetaPackageRestrictionById(POLICY_META_PACKAGE_RESTRICTION_COUNTRY_AND_STATE_ID);

        //Change the country list and get cache updated
        restriction.getCountries().add(CountryCode.US);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());

        //Change the state list and get cache updated
        restriction.getStates().add(StateCode.CA);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    //*****************************************************************************************************************
    //**************************************POLICY META QUOTE PARAM TESTS**********************************************
    //*****************************************************************************************************************
    @Test
    public void policyQuoteParamTest() {
        //Change a policy meta code field and get cache updated
        PolicyQuoteParam policyQuoteParam = getPolicyQuoteParam(POLICY_META_FOR_QUOTE_PARAM_ID, POLICY_QUOTE_PARAM_ID);
        policyQuoteParam.setValue(policyQuoteParam.getValue() + TEST_NUM_ADD);
        policyQuoteParamService.save(policyQuoteParam);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    private PolicyQuoteParam getPolicyQuoteParam(long policyMetaId, long packageId) {
        return policyMetaService.getPolicyMetaById(policyMetaId).getPolicyQuoteParams().stream().filter(c -> c.getId() == packageId).findFirst().get();
    }

    @Test
    public void policyQuoteParamRestrictionTest() {
        //Change a policy meta field and get cache updated
        PolicyQuoteParamRestriction restriction = restrictionService.getPolicyQuoteParamRestrictionById(POLICY_QUOTE_PARAM_RESTRICTION_ID);
        restriction.setMaxValue(restriction.getMaxValue() + TEST_NUM_ADD);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    @Test
    public void policyQuoteParamRestrictionCountryAndStateTest() {

        PolicyQuoteParamRestriction restriction = restrictionService.getPolicyQuoteParamRestrictionById(POLICY_QUOTE_PARAM_RESTRICTION_COUNTRY_AND_STATE_ID);

        //Change the country list and get cache updated
        restriction.getCountries().add(CountryCode.US);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());

        //Change the state list and get cache updated
        restriction.getStates().add(StateCode.CA);
        restrictionService.save(restriction);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    //*****************************************************************************************************************
    //**************************************POLICY VENDOR TESTS********************************************************
    //*****************************************************************************************************************
    @Test
    public void vendorTest() {
        //Change a vendor field and get cache updated
        Vendor vendor = vendorService.getById(VENDOR_ID);
        vendor.setName(vendor.getName() + TEST_SUFFIX);
        vendorService.save(vendor);
        policyMetaCacheService.getPolicyMetas();
        assertTrue(FAIL_UPDATE_MESSAGE, policyMetaCacheService.getUpdated());
    }

    private static final Logger log = LoggerFactory.getLogger(PolicyMetaCacheServiceTest.class);
}