package com.travelinsurancemaster.repository;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestDb {
    private static final Logger log = LoggerFactory.getLogger(TestDb.class);

    @Value("${tim.environment}")
    private String environment;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String mainDbUrl;
    @Value("${spring.datasource.username}")
    private String mainDbUsername;
    @Value("${spring.datasource.password}")
    private String mainDbPass;

    @Value("${spring.secondDatasource.url}")
    private String testDbUrl;
    @Value("${spring.secondDatasource.username}")
    private String testDbUsername;
    @Value("${spring.secondDatasource.password}")
    private String testDbPass;

    private JdbcTemplate testJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void setUp() {
        if (testDbUrl.equals(mainDbUrl)) {
            throw new RuntimeException("Test db should not be the same");
        }
        log.info("Test db " + testDbUrl);
        log.info("Prod db " + mainDbUrl);
        // temporary solution
        DriverManagerDataSource test = new DriverManagerDataSource();
        test.setDriverClassName(driverClassName);
        test.setUrl(testDbUrl);
        test.setUsername(testDbUsername);
        test.setPassword(testDbPass);

        DriverManagerDataSource prod = new DriverManagerDataSource();
        prod.setDriverClassName(driverClassName);
        prod.setUrl(mainDbUrl);
        prod.setUsername(mainDbUsername);
        prod.setPassword(mainDbPass);

        testJdbcTemplate = new JdbcTemplate(test);
        jdbcTemplate = new JdbcTemplate(prod);
    }

    public void sync(Long vendorId) {
        if (!environment.equals("prod")) {
            log.info("Not supported for test");
            return;
        }

        Map<String, Object> vendor = extract("Select * from vendor where id = ", vendorId);
        Map<String, Object> vendorPage = extract("Select * from vendor_page where vendor_id = ", vendorId);
        Map<String, Object> vendorPercentInfo = extract("Select * from vendor_percent_info where vendor_id = ", vendorId);
        List<Map<String, Object>> unsupportedCardTypes = extractList("Select * from unsupported_card_types where vendor_id = " + vendorId);

        List<Map<String, Object>> policiMetas = extractList("Select * from policy_meta where vendor_id = " + vendorId);
        List<Map<String, Object>> policyMetaPages = extractList("Select * from policy_meta_page where vendor_page_id = " + vendorPage.get("id"));


        Map<String, Object> vendorTest = extractTest("Select * from vendor where name = ", (String) vendor.get("name"));
        if(vendorTest.get("id") != null) {
            String vendorTestId = String.valueOf(vendorTest.get("id"));
            Map<String, Object> vendorPageTest = extractTest("Select * from vendor_page where vendor_id = ", vendorTestId);

            List<Map<String, Object>> policiMetasTest = extractListTest("Select * from policy_meta where vendor_id = " + vendorTestId);
            List<Map<String, Object>> policyMetaPagesTest = extractListTest("Select * from policy_meta_page where vendor_page_id = " + vendorPageTest.get("id"));

            clear(vendorTest, vendorPageTest, policiMetasTest, policyMetaPagesTest);
        }
        save(vendor, vendorPage, vendorPercentInfo, unsupportedCardTypes, policiMetas, policyMetaPages);

    }

    private void save(Map<String, Object> vendor,
                      Map<String, Object> vendorPage,
                      Map<String, Object> vendorPercentInfo,
                      List<Map<String, Object>> unsupportedCardTypes,
                      List<Map<String, Object>> policiMetas,
                      List<Map<String, Object>> policyMetaPages) {

        // vendor
        sync(vendor, "vendor");

        // vendor page
        sync(vendorPage, "vendor_page");
        List<Map<String, Object>> filingClaimPages = extractList("Select * from filing_claim_page where vendor_page_id = " + vendorPage.get("id"));
        for (Map<String, Object> filingClaimPage : filingClaimPages) {
            sync(filingClaimPage, "filing_claim_page");
            List<Map<String, Object>> filingClaimContacts = extractList("Select * from filing_claim_contact where filing_claim_page_id = " + filingClaimPage.get("id"));
            for (Map<String, Object> filingClaimContact : filingClaimContacts) {
                sync(filingClaimContact, "filing_claim_contact");
            }

        }

        // vendor percent info
        sync(vendorPercentInfo, "vendor_percent_info");

        // vendor card
        for (Map<String, Object> unsupportedCardType : unsupportedCardTypes) {
            sync(unsupportedCardType, "unsupported_card_types");
        }

        // vendor card
        List<Map<String, Object>> policiMetaCategoryContents = new ArrayList<>();
        for (Map<String, Object> policyMetaPage : policyMetaPages) {
            sync(policyMetaPage, "policy_meta_page");
            Object policiMetaPageId = policyMetaPage.get("id");

            policiMetaCategoryContents.addAll(extractList(String.format(
                    "Select * from policy_meta_category_content where policy_meta_category_id = %s " +
                            "or policy_meta_plan_info_id = %s " +
                            "or policy_meta_custom_category_id = %s " +
                            "or policy_meta_restrictions_id = %s " +
                            "or policy_meta_package_id = %s " +
                            "or package_options_id = %s",
                    policiMetaPageId, policiMetaPageId, policiMetaPageId, policiMetaPageId, policiMetaPageId, policiMetaPageId)));
        }

        // policy_metas
        for (Map<String, Object> policiMeta : policiMetas) {
            sync(policiMeta, "policy_meta");
            syncMetas(policiMeta, "policy_meta_restriction", "restriction_id", "policy_meta_id");

            Object policiMetaId = policiMeta.get("id");
            List<Map<String, Object>> policiQuoteParams = extractList("Select * from policy_quote_param where policy_meta_id = " + policiMetaId);
            for (Map<String, Object> policiQuoteParam : policiQuoteParams) {
                sync(policiQuoteParam, "policy_quote_param");
                syncMetas(policiQuoteParam, "policy_meta_quote_restriction", "policy_quote_param_restriction_id", "policy_quote_param_id");
            }

            List<Map<String, Object>> policiMetaCategories = extractList("Select * from policy_meta_category where policy_meta_id = " + policiMetaId);
            for (Map<String, Object> policiMetaCategory : policiMetaCategories) {
                sync(policiMetaCategory, "policy_meta_category");
                syncMetas(policiMetaCategory, "policy_meta_category_restriction", "category_restriction_id", "policy_meta_category_id");

                List<Map<String, Object>> policiMetaCategoryValues = extractList("Select * from policy_meta_category_value where policy_meta_category_id = " + policiMetaCategory.get("id"));
                for (Map<String, Object> policiMetaCategoryValue : policiMetaCategoryValues) {
                    sync(policiMetaCategoryValue, "policy_meta_category_value");
                    List<Map<String, Object>> subcategoryValues = extractList("Select * from subcategory_value where policy_meta_category_value_id = " + policiMetaCategoryValue.get("id"));
                    for (Map<String, Object> subcategoryValue : subcategoryValues) {
                        syncCategories(subcategoryValue);
                        sync(subcategoryValue, "subcategory_value");
                    }
                }

            }

            List<Map<String, Object>> policiMetaPackages = extractList("Select * from policy_meta_package where policy_meta_id = " + policiMetaId);
            for (Map<String, Object> policiMetaPackage : policiMetaPackages) {
                sync(policiMetaPackage, "policy_meta_package");
                syncMetas(policiMetaPackage, "policy_meta_package_restriction", "package_restriction_id", "policy_meta_package_id");


                List<Map<String, Object>> policiMetaPackageValues = extractList("Select * from policy_meta_package_value where policy_meta_package_id = " + policiMetaPackage.get("id"));
                for (Map<String, Object> policiMetaPackageValue : policiMetaPackageValues) {
                    sync(policiMetaPackageValue, "policy_meta_package_value");
                }
            }

            List<Map<String, Object>> policiMetaCodes = extractList("Select * from policy_meta_code where policy_meta_id = " + policiMetaId);
            for (Map<String, Object> policiMetaCode : policiMetaCodes) {
                sync(policiMetaCode, "policy_meta_code");
                syncMetas(policiMetaCode, "policy_meta_code_restriction", "code_restriction_id", "policy_meta_code_id");
            }

            List<Map<String, Object>> planTypes = extractList("Select * from policy_meta_plan_type where policy_meta_id = " + policiMetaId);
            for (Map<String, Object> planType : planTypes) {
                sync(planType, "policy_meta_plan_type");
            }


        }

        for (Map<String, Object> policiMetaCategoryContent : policiMetaCategoryContents) {
            sync(policiMetaCategoryContent, "policy_meta_category_content");
        }


    }

    private void syncCategories(Map<String, Object> subcategoryValue) {
        Map<String, Object> subcategories = extractTest("Select * from subcategory where id = ", String.valueOf(subcategoryValue.get("subcategory_id")));
        if (subcategories.isEmpty()) {
            List<Map<String, Object>> subcategoriesProd = extractList("Select * from subcategory where id = " + subcategoryValue.get("subcategory_id"));
            for (Map<String, Object> subcategorieProd : subcategoriesProd) {
                sync(subcategorieProd, "subcategory");

                Map<String, Object> categories = extractTest("Select * from category where id = ", String.valueOf(subcategorieProd.get("category_id")));
                if (categories.isEmpty()) {
                    List<Map<String, Object>> categoriesProd = extractList("Select * from category where id = " + subcategorieProd.get("category_id"));
                    for (Map<String, Object> categoryProd : categoriesProd) {
                        Map<String, Object> categoriesValues = extractTest("Select * from category_value where category_id = ", String.valueOf(categoryProd.get("id")));
                        if (categoriesValues.isEmpty()) {
                            sync(extract("Select * from category_value where category_id = ", (Long) categoryProd.get("id")),"category_value");
                        }
                        Map<String, Object> categoriesContents = extractTest("Select * from category_content where category_id = ", String.valueOf(categoryProd.get("id")));
                        if (categoriesContents.isEmpty()) {
                            sync(extract("Select * from category_content where category_id = ", (Long)categoryProd.get("id")),"category_content");
                        }
                    }

                }


            }
        }
    }

    private void clear(Map<String, Object> vendor,
                       Map<String, Object> vendorPage,
                       List<Map<String, Object>> policiMetas,
                       List<Map<String, Object>> policyMetaPages) {

        deleteCustom("vendor_percent_info", vendor.get("id"), "vendor_id");

        // vendor card
        deleteCustom("unsupported_card_types", vendor.get("id"), "vendor_id");

        for (Map<String, Object> policyMetaPage : policyMetaPages) {
            // vendor card
            Object policiMetaPageId = policyMetaPage.get("id");
            List<Map<String, Object>> policiMetaCategoryContents = extractListTest(String.format(
                    "Select * from policy_meta_category_content where policy_meta_category_id = %s " +
                            "or policy_meta_plan_info_id = %s " +
                            "or policy_meta_custom_category_id = %s " +
                            "or policy_meta_restrictions_id = %s " +
                            "or policy_meta_package_id = %s " +
                            "or package_options_id = %s",
                    policiMetaPageId, policiMetaPageId, policiMetaPageId, policiMetaPageId, policiMetaPageId, policiMetaPageId));

            for (Map<String, Object> policiMetaCategoryContent : policiMetaCategoryContents) {
                delete("policy_meta_category_content", policiMetaCategoryContent);
            }
        }

        // policy_metas
        for (Map<String, Object> policiMeta : policiMetas) {
            Object policiMetaId = policiMeta.get("id");
            List<Map<String, Object>> policiQuoteParams = extractListTest("Select * from policy_quote_param where policy_meta_id = " + policiMetaId);
            for (Map<String, Object> policiQuoteParam : policiQuoteParams) {
                deleteMetas(policiQuoteParam, "policy_quote_param_restriction", "policy_quote_param_restriction_id", "policy_quote_param_id");
                delete("policy_quote_param", policiQuoteParam);
            }

            //policy_meta_package
            List<Map<String, Object>> policiMetaPackages = extractListTest("Select * from policy_meta_package where policy_meta_id = " + policiMetaId);
            for (Map<String, Object> policiMetaPackage : policiMetaPackages) {
                List<Map<String, Object>> policiMetaPackageValues = extractListTest("Select * from policy_meta_package_value where policy_meta_package_id = " + policiMetaPackage.get("id"));
                for (Map<String, Object> policiMetaPackageValue : policiMetaPackageValues) {
                    delete("policy_meta_package_value", policiMetaPackageValue);
                }

                deleteMetas(policiMetaPackage, "policy_meta_package_restriction", "package_restriction_id", "policy_meta_package_id");
                delete("policy_meta_package", policiMetaPackage);

            }

            //policy_meta_category
            List<Map<String, Object>> policiMetaCategories = extractListTest("Select * from policy_meta_category where policy_meta_id = " + policiMetaId);
            for (Map<String, Object> policiMetaCategory : policiMetaCategories) {

                List<Map<String, Object>> policiMetaCategoryValues = extractListTest("Select * from policy_meta_category_value where policy_meta_category_id = " + policiMetaCategory.get("id"));

                for (Map<String, Object> policiMetaCategoryValue : policiMetaCategoryValues) {
                    List<Map<String, Object>> subcategoryValues = extractListTest("Select * from subcategory_value where policy_meta_category_value_id = " + policiMetaCategoryValue.get("id"));
                    for (Map<String, Object> subcategoryValue : subcategoryValues) {
                        delete( "subcategory_value", subcategoryValue);
                    }

                    delete( "policy_meta_category_value", policiMetaCategoryValue);
                }

                deleteMetas(policiMetaCategory, "policy_meta_category_restriction", "category_restriction_id", "policy_meta_category_id");
                delete("policy_meta_category", policiMetaCategory);
            }
            deleteCustom("policy_meta_category", policiMetaId,"policy_meta_id");

            //policy_meta_code
            List<Map<String, Object>> policiMetaCodes = extractListTest("Select * from policy_meta_code where policy_meta_id = " + policiMetaId);

            for (Map<String, Object> policiMetaCode : policiMetaCodes) {
                deleteMetas(policiMetaCode, "policy_meta_code_restriction", "code_restriction_id", "policy_meta_code_id");
                delete("policy_meta_code", policiMetaCode);
            }
            deleteCustom("policy_meta_code", policiMetaId, "policy_meta_id");

            deleteMetas(policiMeta, "policy_meta_restriction", "restriction_id", "policy_meta_id");
            List<Map<String, Object>> purchases = extractListTest("Select * from purchase where policy_meta_id = " + policiMetaId);
            for (Map<String, Object> purchase : purchases) {
                deleteCustom("purchase_traveler", purchase.get("id"), "purchase_id");
                deleteCustom("purchase_params", purchase.get("id"), "purchase_id");

                delete("purchase", purchase);

            }
            deleteCustom("purchase", policiMetaId, "policy_meta_id");

            deleteCustom("policy_meta_plan_type", policiMetaId, "policy_meta_id");

            log.warn("Deleting meta " + policiMeta);
            List<Map<String, Object>> policiMetaCategories2 = testJdbcTemplate.queryForList("Select * from policy_meta_category where policy_meta_id = " + policiMetaId);
            log.warn(policiMetaCategories2.toString());
            delete("policy_meta", policiMeta);
        }

        for (Map<String, Object> policyMetaPage : policyMetaPages) {
            // vendor card
            Object policiMetaPageId = policyMetaPage.get("id");
            List<Map<String, Object>> policiMetaCategoryContents = extractListTest(String.format(
                    "Select * from policy_meta_category_content where policy_meta_category_id = %s " +
                            "or policy_meta_plan_info_id = %s " +
                            "or policy_meta_custom_category_id = %s " +
                            "or policy_meta_restrictions_id = %s " +
                            "or policy_meta_package_id = %s " +
                            "or package_options_id = %s",
                    policiMetaPageId, policiMetaPageId, policiMetaPageId, policiMetaPageId, policiMetaPageId, policiMetaPageId));

            for (Map<String, Object> policiMetaCategoryContent : policiMetaCategoryContents) {
                delete("policy_meta_category_content", policiMetaCategoryContent);
            }
            delete("policy_meta_page", policyMetaPage);
        }

        for (Map<String, Object> policyMetaPage : policyMetaPages) {
            delete("policy_meta_page", policyMetaPage);
        }


        List<Map<String, Object>> filingClaimPages = extractListTest("Select * from filing_claim_page where vendor_page_id = " + vendorPage.get("id"));
        for (Map<String, Object> filingClaimPage : filingClaimPages) {
            List<Map<String, Object>> filingClaimContacts = extractListTest("Select * from filing_claim_contact where filing_claim_page_id = " + filingClaimPage.get("id"));
            for (Map<String, Object> filingClaimContact : filingClaimContacts) {
                delete("filing_claim_contact", filingClaimContact);
            }

            delete("filing_claim_page", filingClaimPage);
        }

        delete("vendor_page", vendorPage);

        List<Map<String, Object>> vendorCommissions = extractListTest("Select * from vendor_commission where vendor_id = " + vendor.get("id"));
        for (Map<String, Object> vendorCommission : vendorCommissions) {
            delete("vendor_commission", vendorCommission);
        }

        delete("vendor", vendor);
    }

    private void deleteMetas(Map<String, Object> meta, final String tableName, final String idName, final String mainIdName) {
        List<Map<String, Object>> restrictions = extractListTest("Select * from " + tableName + " where " + mainIdName + " = " + meta.get("id"));
        for (Map<String, Object> restriction : restrictions) {
            List<Map<String, Object>> restrictionCountries = extractListTest("Select * from " + tableName + "_country where " + idName + " = " + restriction.get("id"));
            for (Map<String, Object> restrictionCountry : restrictionCountries) {
                deleteCustom(tableName + "_country", restrictionCountry.get(idName), idName);
            }

            List<Map<String, Object>> restrictionStates = extractListTest("Select * from " + tableName + "_state where " + idName + " = " + restriction.get("id"));
            for (Map<String, Object> restrictionState : restrictionStates) {
                deleteCustom(tableName + "_state", restrictionState.get(idName), idName);
            }

            delete(tableName, restriction);
        }
    }

    private void syncMetas(Map<String, Object> meta, final String tableName, final String idName, final String mainIdName) {
        List<Map<String, Object>> restrictions = extractList("Select * from " + tableName + " where " + mainIdName + " = " + meta.get("id"));
        for (Map<String, Object> restriction : restrictions) {
            sync(restriction, tableName);

            List<Map<String, Object>> restrictionCountries = extractList("Select * from " + tableName + "_country where " + idName + " = " + restriction.get("id"));
            for (Map<String, Object> restrictionCountry : restrictionCountries) {
                sync(restrictionCountry, tableName + "_country");
            }

            List<Map<String, Object>> restrictionStates = extractList("Select * from " + tableName + "_state where " + idName + " = " + restriction.get("id"));
            for (Map<String, Object> restrictionState : restrictionStates) {
                sync(restrictionState, tableName + "_state");
            }

        }
    }

    private void delete(String tableName, Map id) {
        deleteCustom(tableName, id.get("id"), "id");
    }

    void deleteCustom(String tableName, Object idValue, String idName) {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ")
                .append(tableName)
                .append(" where ")
                .append(idName).append(" = '")
                .append(idValue)
                .append("'");

        testJdbcTemplate.update(sb.toString());
    }

    private List<Map<String, Object>> extractList(String id) {
        List<Map<String, Object>> maps;
        try {
            maps = jdbcTemplate.queryForList(id);
        } catch (DataAccessException e) {
            log.error("Unexpected exception", e);
            return Collections.emptyList();
        }
        return maps;
    }

    private List<Map<String, Object>> extractListTest(String id) {
        List<Map<String, Object>> maps;
        try {
            maps = testJdbcTemplate.queryForList(id);
        } catch (DataAccessException e) {
            log.error("Unexpected exception", e);
            return Collections.emptyList();
        }
        return maps;
    }

    private Map<String, Object> extract(String sql, Long vendorId) {
        Map<String, Object> map;
        try {
            map = jdbcTemplate.queryForMap(sql + vendorId);
        } catch (DataAccessException e) {
            log.error("Unexpected exception", e);
            return Collections.emptyMap();
        }

        return map;
    }

    private Map<String, Object> extractTest(String sql, String vendorId) {
        Map<String, Object> map;
        try {
            map = testJdbcTemplate.queryForMap(sql + "'" + vendorId + "'");
        } catch (DataAccessException e) {
            log.error("Unexpected exception", e);
            return Collections.emptyMap();
        }

        return map;
    }

    void sync(Map<String, Object> vendor, String tableName) {
        if (vendor.isEmpty()) {
            return;
        }

        Map<String, String> collect = vendor.entrySet().stream()
                .filter(x -> x.getValue() != null)
                .collect(Collectors.toMap(x -> x.getKey().equals("group") ? "\"group\"" : x.getKey(), x -> "'" + x.getValue().toString().replaceAll("'", "''") + "'"));

        StringBuilder sb = new StringBuilder();
        sb.append("insert into ")
                .append(tableName)
                .append(" (")
                .append(StringUtils.join(collect.keySet(), ","))
                .append(") values(")
                .append(String.join(",", collect.values()))
                .append(")");

        testJdbcTemplate.update(sb.toString());
    }
}
