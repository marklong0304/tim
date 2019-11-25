package com.travelinsurancemaster.services.specifications;

import com.travelinsurancemaster.model.CommissionState;
import com.travelinsurancemaster.model.PaymentCancellationType;
import com.travelinsurancemaster.model.dto.AffiliateCommission;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.UserInfo;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.purchase.CommissionFilter;
import com.travelinsurancemaster.model.dto.purchase.PaymentFilter;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.dto.purchase.SalaryFilter;
import com.travelinsurancemaster.model.dto.report.CommissionReportFilter;
import com.travelinsurancemaster.model.dto.report.PaymentReportFilter;
import com.travelinsurancemaster.model.dto.report.SalaryReportFilter;
import com.travelinsurancemaster.model.dto.report.sales.SalesRequest;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.util.BigDecimalRange;
import com.travelinsurancemaster.model.util.DateRange;
import com.travelinsurancemaster.model.util.IntegerRange;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ritchie on 5/26/15.
 */
@SuppressWarnings("ALL")
public class PurchaseSpecification {

    public static Specification<Purchase> checkParameters(final User user, final Date dateFrom, final Date dateTo, final Vendor vendor, final Purchase.PurchaseType purchaseType) {
        return new Specification<Purchase>() {
            @Override
            public Predicate toPredicate(Root<Purchase> purchase, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(criteriaBuilder.equal(purchase.get("success"), true));
                predicateList.add(criteriaBuilder.equal(purchase.get("purchaseType"), purchaseType));
                if (dateFrom != null && dateTo != null) {
                    predicateList.add(criteriaBuilder.between(purchase.<Date>get("purchaseDate"), dateFrom, dateTo));
                } else if (dateTo == null && dateFrom != null) {
                    predicateList.add(criteriaBuilder.greaterThanOrEqualTo(purchase.<Date>get("purchaseDate"), dateFrom));
                } else if (dateTo != null) {
                    predicateList.add(criteriaBuilder.lessThanOrEqualTo(purchase.<Date>get("purchaseDate"), dateTo));
                }
                if (vendor != null) {
                    predicateList.add(criteriaBuilder.equal(purchase.<String>get("policyMeta").get("vendor"), vendor));
                }
                if (user.getUserInfo() != null && user.getUserInfo().isCompany()) {
                    predicateList.add(criteriaBuilder.equal(purchase.<User>get("affiliateCommission").get("affiliate"), user));
                } else {
                    predicateList.add(criteriaBuilder.equal(purchase.<User>get("user"), user));
                }
                return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
    }

    public static Specification<Purchase> doPaymentFilter(final PaymentFilter filter) {
        return new Specification<Purchase>() {
            @Override
            public Predicate toPredicate(Root<Purchase> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                Join<AffiliateCommission, Purchase> root1 = root.join("affiliateCommission", JoinType.LEFT);
                Join<User, AffiliateCommission> root2 = root1.join("affiliate",  JoinType.LEFT);
                Join<UserInfo, User> root3 = root2.join("userInfo",  JoinType.LEFT);
                predicateList.add(cb.equal(root.get("success"), true));
                if (filter.getCancellation() != null && filter.getCancellation() != PaymentCancellationType.ALL) {
                    if(filter.getCancellation() == PaymentCancellationType.CANCELLED) {
                        predicateList.add(cb.isNotNull(root.<LocalDate>get("cancelled")));
                    } else {
                        predicateList.add(cb.isNull(root.<LocalDate>get("cancelled")));
                    }
                }
                predicateList.add(cb.equal(root.get("purchaseType"), Purchase.PurchaseType.REAL));
                if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                    predicateList.add(root.<User>get("affiliateCommission").get("affiliate").in(filter.getAffiliates()));
                }
                if (CollectionUtils.isNotEmpty(filter.getUsers())) {
                    predicateList.add(root.<User>get("user").in(filter.getUsers()));
                }
                if (CollectionUtils.isNotEmpty(filter.getVendors())) {
                    predicateList.add(root.<Vendor>get("policyMeta").get("vendor").in(filter.getVendors()));
                }
                if (CollectionUtils.isNotEmpty(filter.getPolicies())) {
                    predicateList.add(root.<PolicyMeta>get("policyMeta").in(filter.getPolicies()));
                }
                DateRange departDate = filter.getDepartDate();
                if (departDate.getFrom() != null && departDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getFrom(), departDate.getTo()));
                } else if (departDate.getTo() == null && departDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getFrom()));
                } else if (departDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getTo()));
                }
                DateRange purchaseDate = filter.getPurchaseDate();
                if (purchaseDate.getFrom() != null && purchaseDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("purchaseDate"), purchaseDate.getFrom(), purchaseDate.getTo()));
                } else if (purchaseDate.getTo() == null && purchaseDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getFrom()));
                } else if (purchaseDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getTo()));
                }
                BigDecimalRange tripCost = filter.getTripCost();
                if (tripCost.getFrom() != null && tripCost.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("purchaseQuoteRequest").get("tripCost"), tripCost.getFrom(), tripCost.getTo()));
                } else if (tripCost.getTo() == null && tripCost.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("purchaseQuoteRequest").get("tripCost"), tripCost.getFrom()));
                } else if (tripCost.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("purchaseQuoteRequest").get("tripCost"), tripCost.getTo()));
                }
                BigDecimalRange policyPrice = filter.getPolicyPrice();
                if (policyPrice.getFrom() != null && policyPrice.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("totalPrice"), policyPrice.getFrom(), policyPrice.getTo()));
                } else if (policyPrice.getTo() == null && policyPrice.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("totalPrice"), policyPrice.getFrom()));
                } else if (policyPrice.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("totalPrice"), policyPrice.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getPolicyNumber())) {
                    String policyNumber = "%" + filter.getPolicyNumber().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("policyNumber")), policyNumber));
                }
                if (StringUtils.isNotBlank(filter.getNote())) {
                    String note = "%" + filter.getNote().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("note")), note));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getFirstName())) {
                    String firstName = "%" + filter.getTraveler().getFirstName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName")), firstName));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getMiddleInitials())) {
                    String middleInitials = filter.getTraveler().getMiddleInitials().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("middleInitials")), middleInitials));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getLastName())) {
                    String lastName = "%" + filter.getTraveler().getLastName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName")), lastName));
                }
                IntegerRange age = filter.getTraveler().getAge();
                if (age.getFrom() != null && age.getTo() != null) {
                    predicateList.add(cb.between(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getFrom(), age.getTo()));
                } else if (age.getTo() == null && age.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getFrom()));
                } else if (age.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getSearchKeyword())) {
                    String keyword = "%" + filter.getSearchKeyword().trim().toLowerCase() + "%";
                    Expression<String> exp1 = cb.concat(root2.get("name"), " ");
                    exp1 = cb.concat(exp1, root3.get("lastName"));

                    Expression<String> exp2 = cb.concat(root.<String>get("user").get("name"), " ");
                    exp2 = cb.concat(exp2, root.<String>get("user").get("userInfo").get("lastName"));

                    Expression<String> exp3 = cb.concat(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName"), " ");
                    exp3 = cb.concat(exp3, root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName"));
                    predicateList.add(cb.or(
                            cb.like(cb.lower(exp1), keyword),
                            cb.like(cb.lower(exp2), keyword),
                            cb.like(cb.lower(exp3), keyword),
                            cb.like(cb.lower(root.<String>get("policyMeta").get("vendor").get("name")), keyword),
                            cb.like(cb.lower(root.<String>get("policyNumber")), keyword),
                            cb.like(root.<Date>get("purchaseDate").as(String.class), keyword),
                            cb.like(root.<BigDecimal>get("totalPrice").as(String.class), keyword),
                            cb.like(cb.lower(exp3), keyword),
                            cb.like(root.<BigDecimal>get("purchaseQuoteRequest").get("tripCost").as(String.class), keyword),
                            cb.like(root.<Date>get("purchaseQuoteRequest").get("departDate").as(String.class), keyword),
                            cb.like(cb.lower(root.<String>get("note")), keyword)
                    ));
                }
                if (filter.isNoAffiliate()) {
                    predicateList.add(root.get("affiliateCommission").isNull());
                }
                //predicateList.add( cb.isNull(root.<User>get("user").get("deleted")));
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
    }

    public static Specification<Purchase> doCommissionFilter(final CommissionFilter filter) {
        return new Specification<Purchase>() {
            @Override
            public Predicate toPredicate(Root<Purchase> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                Join<AffiliateCommission, Purchase> root1 = root.join("affiliateCommission", JoinType.LEFT);
                Join<User, AffiliateCommission> root2 = root1.join("affiliate",  JoinType.LEFT);
                Join<UserInfo, User> root3 = root2.join("userInfo",  JoinType.LEFT);
                predicateList.add(cb.equal(root.get("success"), true));
                if (filter.isCancellation() != null) {
                    if(filter.isCancellation()) {
                        predicateList.add(cb.isNotNull(root.<LocalDate>get("cancelled")));
                    } else {
                        predicateList.add(cb.isNull(root.<LocalDate>get("cancelled")));
                    }
                }
                predicateList.add(cb.equal(root.get("purchaseType"), Purchase.PurchaseType.REAL));
                if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                    predicateList.add(root.<User>get("affiliateCommission").get("affiliate").in(filter.getAffiliates()));
                }
                if (CollectionUtils.isNotEmpty(filter.getUsers())) {
                    predicateList.add(root.<User>get("user").in(filter.getUsers()));
                }
                if (CollectionUtils.isNotEmpty(filter.getVendors())) {
                    predicateList.add(root.<Vendor>get("policyMeta").get("vendor").in(filter.getVendors()));
                }
                if (CollectionUtils.isNotEmpty(filter.getPolicies())) {
                    predicateList.add(root.<PolicyMeta>get("policyMeta").in(filter.getPolicies()));
                }
                DateRange departDate = filter.getDepartDate();
                if (departDate.getFrom() != null && departDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getFrom(), departDate.getTo()));
                } else if (departDate.getTo() == null && departDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getFrom()));
                } else if (departDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getTo()));
                }
                DateRange purchaseDate = filter.getPurchaseDate();
                if (purchaseDate.getFrom() != null && purchaseDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("purchaseDate"), purchaseDate.getFrom(), purchaseDate.getTo()));
                } else if (purchaseDate.getTo() == null && purchaseDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getFrom()));
                } else if (purchaseDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getPolicyNumber())) {
                    String policyNumber = "%" + filter.getPolicyNumber().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("policyNumber")), policyNumber));
                }
                if (StringUtils.isNotBlank(filter.getNote())) {
                    String note = "%" + filter.getNote().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("note")), note));
                }
                BigDecimalRange totalPrice = filter.getTotalPrice();
                if (totalPrice.getFrom() != null && totalPrice.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("totalPrice"), totalPrice.getFrom(), totalPrice.getTo()));
                } else if (totalPrice.getTo() == null && totalPrice.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("totalPrice"), totalPrice.getFrom()));
                } else if (totalPrice.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("totalPrice"), totalPrice.getTo()));
                }
                BigDecimalRange expectedCommission = filter.getExpectedCommission();
                if (expectedCommission.getFrom() != null && expectedCommission.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("vendorCommission").get("expectedCommission"), expectedCommission.getFrom(), expectedCommission.getTo()));
                } else if (expectedCommission.getTo() == null && expectedCommission.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("vendorCommission").get("expectedCommission"), expectedCommission.getFrom()));
                } else if (expectedCommission.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("vendorCommission").get("expectedCommission"), expectedCommission.getTo()));
                }
                BigDecimalRange receivedCommission = filter.getReceivedCommission();
                if (receivedCommission.getFrom() != null && receivedCommission.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("vendorCommission").get("receivedCommission"), receivedCommission.getFrom(), receivedCommission.getTo()));
                } else if (receivedCommission.getTo() == null && receivedCommission.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("vendorCommission").get("receivedCommission"), receivedCommission.getFrom()));
                } else if (receivedCommission.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("vendorCommission").get("receivedCommission"), receivedCommission.getTo()));
                }
                DateRange receivedDate = filter.getReceivedDate();
                if (receivedDate.getFrom() != null && receivedDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("vendorCommission").get("receivedDate"), receivedDate.getFrom(), receivedDate.getTo()));
                } else if (receivedDate.getTo() == null && receivedDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("vendorCommission").get("receivedDate"), receivedDate.getFrom()));
                } else if (receivedDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("vendorCommission").get("receivedDate"), receivedDate.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getCheckNumber())) {
                    String checkNumber = "%" + filter.getCheckNumber().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("vendorCommission").get("checkNumber")), checkNumber));
                }
                if (filter.getConfirm() != null && CommissionState.ALL != filter.getConfirm()) {
                    predicateList.add(cb.equal(root.<Boolean>get("vendorCommission").get("confirm"),
                            CommissionState.RCVD == filter.getConfirm()));
                }

                if (StringUtils.isNotBlank(filter.getTraveler().getFirstName())) {
                    String firstName = "%" + filter.getTraveler().getFirstName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName")), firstName));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getMiddleInitials())) {
                    String middleInitials = "%" + filter.getTraveler().getMiddleInitials().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("middleInitials")), middleInitials));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getLastName())) {
                    String lastName = "%" + filter.getTraveler().getLastName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName")), lastName));
                }
                IntegerRange age = filter.getTraveler().getAge();
                if (age.getFrom() != null && age.getTo() != null) {
                    predicateList.add(cb.between(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getFrom(), age.getTo()));
                } else if (age.getTo() == null && age.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getFrom()));
                } else if (age.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getSearchKeyword())) {
                    String keyword = "%" + filter.getSearchKeyword().trim().toLowerCase() + "%";
                    Expression<String> exp1 = cb.concat(root2.get("name"), " ");
                    exp1 = cb.concat(exp1, root3.get("lastName"));

                    Expression<String> exp2 = cb.concat(root.<String>get("user").get("name"), " ");
                    exp2 = cb.concat(exp2, root.<String>get("user").get("userInfo").get("lastName"));

                    Expression<String> exp3 = cb.concat(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName"), " ");
                    exp3 = cb.concat(exp3, root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName"));

                    predicateList.add(cb.or(
                            cb.like(cb.lower(exp1), keyword),
                            cb.like(cb.lower(exp2), keyword),
                            cb.like(cb.lower(exp3), keyword),
                            cb.like(cb.lower(root.<String>get("policyMeta").get("vendor").get("name")), keyword),
                            cb.like(cb.lower(root.<String>get("policyMeta").get("displayName")), keyword),
                            cb.like(cb.lower(root.<String>get("policyNumber")), keyword),
                            cb.like(root.<Date>get("purchaseQuoteRequest").get("departDate").as(String.class), keyword),
                            cb.like(root.<Date>get("purchaseDate").as(String.class), keyword),
                            cb.like(root.<BigDecimal>get("totalPrice").as(String.class), keyword),
                            cb.like(root.<BigDecimal>get("vendorCommission").get("expectedCommission").as(String.class), keyword),
                            cb.like(cb.lower(root.<String>get("vendorCommission").get("checkNumber")), keyword),
                            cb.like(root.<BigDecimal>get("vendorCommission").get("receivedCommission").as(String.class), keyword),
                            cb.like(root.<Date>get("vendorCommission").get("receivedDate").as(String.class), keyword),
                            cb.like(cb.lower(root.<String>get("note")), keyword)
                    ));
                }
                if (filter.isNoAffiliate()) {
                    predicateList.add(root.get("affiliateCommission").isNull());
                };
                //predicateList.add( cb.isNull(root.<User>get("user").get("deleted")));
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
    }

    public static Specification<Purchase> doSalaryFilter(final SalaryFilter filter) {
        return new Specification<Purchase>() {
            @Override
            public Predicate toPredicate(Root<Purchase> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(cb.equal(root.get("success"), true));
                if (filter.isCancellation() != null) {
                    if(filter.isCancellation()) {
                        predicateList.add(cb.isNotNull(root.<LocalDate>get("cancelled")));
                    } else {
                        predicateList.add(cb.isNull(root.<LocalDate>get("cancelled")));
                    }
                }
                if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                    predicateList.add(root.<User>get("affiliateCommission").get("affiliate").in(filter.getAffiliates()));
                }
                if (CollectionUtils.isNotEmpty(filter.getVendors())) {
                    predicateList.add(root.<Vendor>get("policyMeta").get("vendor").in(filter.getVendors()));
                }
                if (CollectionUtils.isNotEmpty(filter.getPolicies())) {
                    predicateList.add(root.<PolicyMeta>get("policyMeta").in(filter.getPolicies()));
                }
                DateRange purchaseDate = filter.getPurchaseDate();
                if (purchaseDate.getFrom() != null && purchaseDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("purchaseDate"), purchaseDate.getFrom(), purchaseDate.getTo()));
                } else if (purchaseDate.getTo() == null && purchaseDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getFrom()));
                } else if (purchaseDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getPolicyNumber())) {
                    String policyNumber = "%" + filter.getPolicyNumber().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("policyNumber")), policyNumber));
                }
                if (StringUtils.isNotBlank(filter.getNote())) {
                    String note = "%" + filter.getNote().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("note")), note));
                }
                BigDecimalRange expectedSalary = filter.getExpectedSalary();
                if (expectedSalary.getFrom() != null && expectedSalary.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("affiliateCommission").get("salary"), expectedSalary.getFrom(), expectedSalary.getTo()));
                } else if (expectedSalary.getTo() == null && expectedSalary.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("affiliateCommission").get("salary"), expectedSalary.getFrom()));
                } else if (expectedSalary.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("affiliateCommission").get("salary"), expectedSalary.getTo()));
                }
                BigDecimalRange salary = filter.getSalary();
                if (salary.getFrom()!= null && salary.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("affiliateCommission").get("salaryToPay"), salary.getFrom(), salary.getTo()));
                } else if (salary.getTo() == null && salary.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("affiliateCommission").get("salaryToPay"), salary.getFrom()));
                } else if (salary.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("affiliateCommission").get("salaryToPay"), salary.getTo()));
                }
                BigDecimalRange totalPrice = filter.getTotalPrice();
                if (totalPrice.getFrom()!= null && totalPrice.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("totalPrice"), totalPrice.getFrom(), totalPrice.getTo()));
                } else if (totalPrice.getTo() == null && totalPrice.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("totalPrice"), totalPrice.getFrom()));
                } else if (totalPrice.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("totalPrice"), totalPrice.getTo()));
                }
                BigDecimalRange receivedCommission = filter.getReceivedCommission();
                if (receivedCommission.getFrom() != null && receivedCommission.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("vendorCommission").get("receivedCommission"), receivedCommission.getFrom(), receivedCommission.getTo()));
                } else if (receivedCommission.getTo() == null && receivedCommission.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("vendorCommission").get("receivedCommission"), receivedCommission.getFrom()));
                } else if (receivedCommission.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("vendorCommission").get("receivedCommission"), receivedCommission.getTo()));
                }
                if (filter.isPay() != null) {
                    predicateList.add(filter.isPay() ? cb.isNotNull(root.get("affiliateCommission").get("paid")) : cb.isNull(root.get("affiliateCommission").get("paid")));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getFirstName())) {
                    String firstName = "%" + filter.getTraveler().getFirstName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName")), firstName));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getMiddleInitials())) {
                    String middleInitials = "%" + filter.getTraveler().getMiddleInitials().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("middleInitials")), middleInitials));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getLastName())) {
                    String lastName = "%" + filter.getTraveler().getLastName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName")), lastName));
                }
                if (StringUtils.isNotBlank(filter.getSearchKeyword())) {
                    String keyword = "%" + filter.getSearchKeyword().trim().toLowerCase() + "%";
                    Expression<String> exp1 = cb.concat(root.<String>get("affiliateCommission").get("affiliate").get("name"), " ");
                    exp1 = cb.concat(exp1, root.<String>get("affiliateCommission").get("affiliate").get("userInfo").get("lastName"));

                    Expression<String> exp2 = cb.concat(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName"), " ");
                    exp2 = cb.concat(exp2, root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName"));
                    predicateList.add(cb.or(
                            cb.like(cb.lower(exp1), keyword),
                            //cb.like(cb.lower(exp2), keyword),
                            cb.like(cb.lower(root.<String>get("policyMeta").get("vendor").get("name")), keyword),
                            cb.like(cb.lower(root.<String>get("policyMeta").get("displayName")), keyword),
                            cb.like(cb.lower(root.<String>get("policyNumber")), keyword),
                            cb.like(root.<BigDecimal>get("totalPrice").as(String.class), keyword),
                            cb.like(root.<BigDecimal>get("vendorCommission").get("receivedCommission").as(String.class), keyword),
                            cb.like(root.<BigDecimal>get("affiliateCommission").get("salary").as(String.class), keyword),
                            cb.like(cb.lower(root.<String>get("note")), keyword)
                    ));
                }
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
    }

    public static Specification<Purchase> doSalarySearchFilter(final SalaryFilter filter) {
        return new Specification<Purchase>() {
            @Override
            public Predicate toPredicate(Root<Purchase> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(cb.equal(root.get("success"), true));
                predicateList.add(cb.isNotNull((root.get("affiliateCommission"))));
                predicateList.add(cb.isNotNull((root.get("affiliateCommission").get("paid"))));

                if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                    predicateList.add(root.<User>get("affiliateCommission").get("affiliate").in(filter.getAffiliates()));
                }

                DateRange payDate = filter.getPayDate();
                if (payDate.getFrom() != null && payDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("affiliateCommission").get("paid"), payDate.getFrom(), payDate.getTo()));
                } else if (payDate.getTo() == null && payDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("affiliateCommission").get("paid"), payDate.getFrom()));
                } else if (payDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("affiliateCommission").get("paid"), payDate.getTo()));
                }

                BigDecimalRange salary = filter.getSalary();
                if (salary.getFrom()!= null && salary.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("affiliateCommission").get("salaryToPay"), salary.getFrom(), salary.getTo()));
                } else if (salary.getTo() == null && salary.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("affiliateCommission").get("salaryToPay"), salary.getFrom()));
                } else if (salary.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("affiliateCommission").get("salaryToPay"), salary.getTo()));
                }

                if (StringUtils.isNotBlank(filter.getSearchKeyword())) {
                    String keyword = "%" + filter.getSearchKeyword().trim().toLowerCase() + "%";
                    Expression<String> exp1 = cb.concat(root.<String>get("affiliateCommission").get("affiliate").get("name"), " ");
                    exp1 = cb.concat(exp1, root.<String>get("affiliateCommission").get("affiliate").get("userInfo").get("lastName"));

                    predicateList.add(cb.or(
                            cb.like(cb.lower(exp1), keyword),
                            cb.like(root.<BigDecimal>get("affiliateCommission").get("salary").as(String.class), keyword),
                            cb.like(cb.lower(root.<User>get("affiliateCommission").get("affiliate").get("company").get("name")), keyword)
                    ));
                }
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
    }

    public static Specification<Purchase> doSalaryReportFilter(final SalaryReportFilter filter) {
        return new Specification<Purchase>() {
            @Override
            public Predicate toPredicate(Root<Purchase> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(cb.equal(root.get("success"), true));
                if (filter.isCancellation() != null) {
                    if(filter.isCancellation()) {
                        predicateList.add(cb.isNotNull(root.<LocalDate>get("cancelled")));
                    } else {
                        predicateList.add(cb.isNull(root.<LocalDate>get("cancelled")));
                    }
                }
                if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                    predicateList.add(root.<User>get("affiliateCommission").get("affiliate").in(filter.getAffiliates()));
                }
                if (CollectionUtils.isNotEmpty(filter.getVendors())) {
                    predicateList.add(root.<Vendor>get("policyMeta").get("vendor").in(filter.getVendors()));
                }
                if (CollectionUtils.isNotEmpty(filter.getPolicies())) {
                    predicateList.add(root.<PolicyMeta>get("policyMeta").in(filter.getPolicies()));
                }
                DateRange purchaseDate = filter.getPurchaseDate();
                if (purchaseDate.getFrom() != null && purchaseDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("purchaseDate"), purchaseDate.getFrom(), purchaseDate.getTo()));
                } else if (purchaseDate.getTo() == null && purchaseDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getFrom()));
                } else if (purchaseDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getPolicyNumber())) {
                    String policyNumber = "%" + filter.getPolicyNumber().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("policyNumber")), policyNumber));
                }
                if (StringUtils.isNotBlank(filter.getNote())) {
                    String note = "%" + filter.getNote().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("note")), note));
                }
                BigDecimalRange expectedSalary = filter.getExpectedSalary();
                if (expectedSalary.getFrom() != null && expectedSalary.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("affiliateCommission").get("salary"), expectedSalary.getFrom(), expectedSalary.getTo()));
                } else if (expectedSalary.getTo() == null && expectedSalary.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("affiliateCommission").get("salary"), expectedSalary.getFrom()));
                } else if (expectedSalary.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("affiliateCommission").get("salary"), expectedSalary.getTo()));
                }
                BigDecimalRange salary = filter.getSalary();
                if (salary.getFrom()!= null && salary.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("affiliateCommission").get("salaryToPay"), salary.getFrom(), salary.getTo()));
                } else if (salary.getTo() == null && salary.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("affiliateCommission").get("salaryToPay"), salary.getFrom()));
                } else if (salary.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("affiliateCommission").get("salaryToPay"), salary.getTo()));
                }
                BigDecimalRange totalPrice = filter.getTotalPrice();
                if (totalPrice.getFrom()!= null && totalPrice.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("totalPrice"), totalPrice.getFrom(), totalPrice.getTo()));
                } else if (totalPrice.getTo() == null && totalPrice.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("totalPrice"), totalPrice.getFrom()));
                } else if (totalPrice.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("totalPrice"), totalPrice.getTo()));
                }
                if (filter.isPay() != null) {
                    predicateList.add(filter.isPay() ? cb.isNotNull(root.get("affiliateCommission").get("paid")) : cb.isNull(root.get("affiliateCommission").get("paid")));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getFirstName())) {
                    String firstName = "%" + filter.getTraveler().getFirstName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName")), firstName));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getMiddleInitials())) {
                    String middleInitials = "%" + filter.getTraveler().getMiddleInitials().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("middleInitials")), middleInitials));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getLastName())) {
                    String lastName = "%" + filter.getTraveler().getLastName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName")), lastName));
                }
                if (StringUtils.isNotBlank(filter.getSearchKeyword())) {
                    String keyword = "%" + filter.getSearchKeyword().trim().toLowerCase() + "%";
                    Expression<String> exp1 = cb.concat(root.<String>get("affiliateCommission").get("affiliate").get("name"), " ");
                    exp1 = cb.concat(exp1, root.<String>get("affiliateCommission").get("affiliate").get("userInfo").get("lastName"));

                    Expression<String> exp2 = cb.concat(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName"), " ");
                    exp2 = cb.concat(exp2, root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName"));
                    predicateList.add(cb.or(
                            cb.like(cb.lower(exp1), keyword),
                            //cb.like(cb.lower(exp2), keyword),
                            cb.like(cb.lower(root.<String>get("policyMeta").get("vendor").get("name")), keyword),
                            cb.like(cb.lower(root.<String>get("policyMeta").get("displayName")), keyword),
                            cb.like(cb.lower(root.<String>get("policyNumber")), keyword),
                            cb.like(root.<BigDecimal>get("totalPrice").as(String.class), keyword),
                            cb.like(root.<BigDecimal>get("affiliateCommission").get("salary").as(String.class), keyword),
                            cb.like(cb.lower(root.<String>get("note")), keyword)
                    ));
                }
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
    }

    public static Specification<Purchase> doPaymentReportFilter(final PaymentReportFilter filter) {
        return new Specification<Purchase>() {
            @Override
            public Predicate toPredicate(Root<Purchase> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                Join<AffiliateCommission, Purchase> root1 = root.join("affiliateCommission", JoinType.LEFT);
                Join<User, AffiliateCommission> root2 = root1.join("affiliate",  JoinType.LEFT);
                Join<UserInfo, User> root3 = root2.join("userInfo",  JoinType.LEFT);
                predicateList.add(cb.equal(root.get("success"), true));
                if (filter.getCancellation() != null && filter.getCancellation() != PaymentCancellationType.ALL) {
                    if(filter.getCancellation() == PaymentCancellationType.CANCELLED) {
                        predicateList.add(cb.isNotNull(root.<LocalDate>get("cancelled")));
                    } else {
                        predicateList.add(cb.isNull(root.<LocalDate>get("cancelled")));
                    }
                }
                predicateList.add(cb.equal(root.get("purchaseType"), Purchase.PurchaseType.REAL));
                if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                    predicateList.add(root.<User>get("affiliateCommission").get("affiliate").in(filter.getAffiliates()));
                }
                if (CollectionUtils.isNotEmpty(filter.getUsers())) {
                    predicateList.add(root.<User>get("user").in(filter.getUsers()));
                }
                if (CollectionUtils.isNotEmpty(filter.getVendors())) {
                    predicateList.add(root.<Vendor>get("policyMeta").get("vendor").in(filter.getVendors()));
                }
                if (CollectionUtils.isNotEmpty(filter.getPolicies())) {
                    predicateList.add(root.<PolicyMeta>get("policyMeta").in(filter.getPolicies()));
                }
                DateRange departDate = filter.getDepartDate();
                if (departDate.getFrom() != null && departDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getFrom(), departDate.getTo()));
                } else if (departDate.getTo() == null && departDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getFrom()));
                } else if (departDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getTo()));
                }
                DateRange purchaseDate = filter.getPurchaseDate();
                if (purchaseDate.getFrom() != null && purchaseDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("purchaseDate"), purchaseDate.getFrom(), purchaseDate.getTo()));
                } else if (purchaseDate.getTo() == null && purchaseDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getFrom()));
                } else if (purchaseDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getTo()));
                }
                BigDecimalRange tripCost = filter.getTripCost();
                if (tripCost.getFrom() != null && tripCost.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("purchaseQuoteRequest").get("tripCost"), tripCost.getFrom(), tripCost.getTo()));
                } else if (tripCost.getTo() == null && tripCost.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("purchaseQuoteRequest").get("tripCost"), tripCost.getFrom()));
                } else if (tripCost.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("purchaseQuoteRequest").get("tripCost"), tripCost.getTo()));
                }
                BigDecimalRange policyPrice = filter.getPolicyPrice();
                if (policyPrice.getFrom() != null && policyPrice.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("totalPrice"), policyPrice.getFrom(), policyPrice.getTo()));
                } else if (policyPrice.getTo() == null && policyPrice.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("totalPrice"), policyPrice.getFrom()));
                } else if (policyPrice.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("totalPrice"), policyPrice.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getPolicyNumber())) {
                    String policyNumber = "%" + filter.getPolicyNumber().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("policyNumber")), policyNumber));
                }
                if (StringUtils.isNotBlank(filter.getNote())) {
                    String note = "%" + filter.getNote().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("note")), note));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getFirstName())) {
                    String firstName = "%" + filter.getTraveler().getFirstName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName")), firstName));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getMiddleInitials())) {
                    String middleInitials = filter.getTraveler().getMiddleInitials().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("middleInitials")), middleInitials));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getLastName())) {
                    String lastName = "%" + filter.getTraveler().getLastName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName")), lastName));
                }
                IntegerRange age = filter.getTraveler().getAge();
                if (age.getFrom() != null && age.getTo() != null) {
                    predicateList.add(cb.between(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getFrom(), age.getTo()));
                } else if (age.getTo() == null && age.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getFrom()));
                } else if (age.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getSearchKeyword())) {
                    String keyword = "%" + filter.getSearchKeyword().trim().toLowerCase() + "%";
                    Expression<String> exp1 = cb.concat(root2.get("name"), " ");
                    exp1 = cb.concat(exp1, root3.get("lastName"));

                    Expression<String> exp2 = cb.concat(root.<String>get("user").get("name"), " ");
                    exp2 = cb.concat(exp2, root.<String>get("user").get("userInfo").get("lastName"));

                    Expression<String> exp3 = cb.concat(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName"), " ");
                    exp3 = cb.concat(exp3, root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName"));
                    predicateList.add(cb.or(
                            cb.like(cb.lower(exp1), keyword),
                            cb.like(cb.lower(exp2), keyword),
                            cb.like(cb.lower(exp3), keyword),
                            cb.like(cb.lower(root.<String>get("policyMeta").get("vendor").get("name")), keyword),
                            cb.like(cb.lower(root.<String>get("policyNumber")), keyword),
                            cb.like(root.<Date>get("purchaseDate").as(String.class), keyword),
                            cb.like(root.<BigDecimal>get("totalPrice").as(String.class), keyword),
                            cb.like(cb.lower(exp3), keyword),
                            cb.like(root.<BigDecimal>get("purchaseQuoteRequest").get("tripCost").as(String.class), keyword),
                            cb.like(root.<Date>get("purchaseQuoteRequest").get("departDate").as(String.class), keyword),
                            cb.like(cb.lower(root.<String>get("note")), keyword)
                    ));
                }
                if (filter.isNoAffiliate()) {
                    predicateList.add(root.get("affiliateCommission").isNull());
                }
                //predicateList.add( cb.isNull(root.<User>get("user").get("deleted")));
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
    }

    public static Specification<Purchase> doCommissionReportFilter(final CommissionReportFilter filter) {
        return new Specification<Purchase>() {
            @Override
            public Predicate toPredicate(Root<Purchase> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                Join<AffiliateCommission, Purchase> root1 = root.join("affiliateCommission", JoinType.LEFT);
                Join<User, AffiliateCommission> root2 = root1.join("affiliate",  JoinType.LEFT);
                Join<UserInfo, User> root3 = root2.join("userInfo",  JoinType.LEFT);
                predicateList.add(cb.equal(root.get("success"), true));
                if (filter.isCancellation() != null) {
                    if(filter.isCancellation()) {
                        predicateList.add(cb.isNotNull(root.<LocalDate>get("cancelled")));
                    } else {
                        predicateList.add(cb.isNull(root.<LocalDate>get("cancelled")));
                    }
                }
                predicateList.add(cb.equal(root.get("purchaseType"), Purchase.PurchaseType.REAL));
                if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                    predicateList.add(root.<User>get("affiliateCommission").get("affiliate").in(filter.getAffiliates()));
                }
                if (CollectionUtils.isNotEmpty(filter.getUsers())) {
                    predicateList.add(root.<User>get("user").in(filter.getUsers()));
                }
                if (CollectionUtils.isNotEmpty(filter.getVendors())) {
                    predicateList.add(root.<Vendor>get("policyMeta").get("vendor").in(filter.getVendors()));
                }
                if (CollectionUtils.isNotEmpty(filter.getPolicies())) {
                    predicateList.add(root.<PolicyMeta>get("policyMeta").in(filter.getPolicies()));
                }
                DateRange departDate = filter.getDepartDate();
                if (departDate.getFrom() != null && departDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getFrom(), departDate.getTo()));
                } else if (departDate.getTo() == null && departDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getFrom()));
                } else if (departDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getTo()));
                }
                DateRange purchaseDate = filter.getPurchaseDate();
                if (purchaseDate.getFrom() != null && purchaseDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("purchaseDate"), purchaseDate.getFrom(), purchaseDate.getTo()));
                } else if (purchaseDate.getTo() == null && purchaseDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getFrom()));
                } else if (purchaseDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("purchaseDate"), purchaseDate.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getPolicyNumber())) {
                    String policyNumber = "%" + filter.getPolicyNumber().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("policyNumber")), policyNumber));
                }
                if (StringUtils.isNotBlank(filter.getNote())) {
                    String note = "%" + filter.getNote().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("note")), note));
                }
                BigDecimalRange totalPrice = filter.getTotalPrice();
                if (totalPrice.getFrom() != null && totalPrice.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("totalPrice"), totalPrice.getFrom(), totalPrice.getTo()));
                } else if (totalPrice.getTo() == null && totalPrice.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("totalPrice"), totalPrice.getFrom()));
                } else if (totalPrice.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("totalPrice"), totalPrice.getTo()));
                }
                BigDecimalRange expectedCommission = filter.getExpectedCommission();
                if (expectedCommission.getFrom() != null && expectedCommission.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("vendorCommission").get("expectedCommission"), expectedCommission.getFrom(), expectedCommission.getTo()));
                } else if (expectedCommission.getTo() == null && expectedCommission.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("vendorCommission").get("expectedCommission"), expectedCommission.getFrom()));
                } else if (expectedCommission.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("vendorCommission").get("expectedCommission"), expectedCommission.getTo()));
                }
                BigDecimalRange receivedCommission = filter.getReceivedCommission();
                if (receivedCommission.getFrom() != null && receivedCommission.getTo() != null) {
                    predicateList.add(cb.between(root.<BigDecimal>get("vendorCommission").get("receivedCommission"), receivedCommission.getFrom(), receivedCommission.getTo()));
                } else if (receivedCommission.getTo() == null && receivedCommission.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("vendorCommission").get("receivedCommission"), receivedCommission.getFrom()));
                } else if (receivedCommission.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("vendorCommission").get("receivedCommission"), receivedCommission.getTo()));
                }
                DateRange receivedDate = filter.getReceivedDate();
                if (receivedDate.getFrom() != null && receivedDate.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("vendorCommission").get("receivedDate"), receivedDate.getFrom(), receivedDate.getTo()));
                } else if (receivedDate.getTo() == null && receivedDate.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("vendorCommission").get("receivedDate"), receivedDate.getFrom()));
                } else if (receivedDate.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("vendorCommission").get("receivedDate"), receivedDate.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getCheckNumber())) {
                    String checkNumber = "%" + filter.getCheckNumber().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("vendorCommission").get("checkNumber")), checkNumber));
                }
                if (filter.getConfirm() != null && CommissionState.ALL != filter.getConfirm()) {
                    predicateList.add(cb.equal(root.<Boolean>get("vendorCommission").get("confirm"),
                            CommissionState.RCVD == filter.getConfirm()));
                }

                if (StringUtils.isNotBlank(filter.getTraveler().getFirstName())) {
                    String firstName = "%" + filter.getTraveler().getFirstName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName")), firstName));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getMiddleInitials())) {
                    String middleInitials = "%" + filter.getTraveler().getMiddleInitials().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("middleInitials")), middleInitials));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getLastName())) {
                    String lastName = "%" + filter.getTraveler().getLastName().trim().toLowerCase() + "%";
                    predicateList.add(cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName")), lastName));
                }
                IntegerRange age = filter.getTraveler().getAge();
                if (age.getFrom() != null && age.getTo() != null) {
                    predicateList.add(cb.between(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getFrom(), age.getTo()));
                } else if (age.getTo() == null && age.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getFrom()));
                } else if (age.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getSearchKeyword())) {
                    String keyword = "%" + filter.getSearchKeyword().trim().toLowerCase() + "%";
                    Expression<String> exp1 = cb.concat(root2.get("name"), " ");
                    exp1 = cb.concat(exp1, root3.get("lastName"));

                    Expression<String> exp2 = cb.concat(root.<String>get("user").get("name"), " ");
                    exp2 = cb.concat(exp2, root.<String>get("user").get("userInfo").get("lastName"));

                    Expression<String> exp3 = cb.concat(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName"), " ");
                    exp3 = cb.concat(exp3, root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName"));

                    predicateList.add(cb.or(
                            cb.like(cb.lower(exp1), keyword),
                            cb.like(cb.lower(exp2), keyword),
                            cb.like(cb.lower(exp3), keyword),
                            cb.like(cb.lower(root.<String>get("policyMeta").get("vendor").get("name")), keyword),
                            cb.like(cb.lower(root.<String>get("policyMeta").get("displayName")), keyword),
                            cb.like(cb.lower(root.<String>get("policyNumber")), keyword),
                            cb.like(root.<Date>get("purchaseQuoteRequest").get("departDate").as(String.class), keyword),
                            cb.like(root.<Date>get("purchaseDate").as(String.class), keyword),
                            cb.like(root.<BigDecimal>get("totalPrice").as(String.class), keyword),
                            cb.like(root.<BigDecimal>get("vendorCommission").get("expectedCommission").as(String.class), keyword),
                            cb.like(cb.lower(root.<String>get("vendorCommission").get("checkNumber")), keyword),
                            cb.like(root.<BigDecimal>get("vendorCommission").get("receivedCommission").as(String.class), keyword),
                            cb.like(root.<Date>get("vendorCommission").get("receivedDate").as(String.class), keyword),
                            cb.like(cb.lower(root.<String>get("note")), keyword)
                    ));
                }
                if (filter.isNoAffiliate()) {
                    predicateList.add(root.get("affiliateCommission").isNull());
                };
                //predicateList.add( cb.isNull(root.<User>get("user").get("deleted")));
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
    }

    public static Specification<Purchase> doSalesFilter(final SalesRequest filter) {
        return new Specification<Purchase>() {
            @Override
            public Predicate toPredicate(Root<Purchase> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<>();
                predicateList.add(cb.equal(root.get("success"), true));
                predicateList.add(cb.equal(root.<Boolean>get("vendorCommission").get("confirm"), true));
                predicateList.add(cb.equal(root.get("purchaseType"), Purchase.PurchaseType.REAL));

                if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                    predicateList.add(root.<User>get("affiliateCommission").get("affiliate").in(filter.getAffiliates()));
                }
                if (filter.getFrom() != null && filter.getTo() != null) {
                    predicateList.add(cb.between(root.<Date>get("purchaseDate"), filter.getFrom(), filter.getTo()));
                } else if (filter.getTo() == null && filter.getFrom() != null) {
                    predicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("purchaseDate"), filter.getFrom()));
                } else if (filter.getTo() != null) {
                    predicateList.add(cb.lessThanOrEqualTo(root.<Date>get("purchaseDate"), filter.getTo()));
                }
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
    }

    public static Specification<Purchase> getTravellersOfSuccessPurchase(String query) {
        return (root, criteriaQuery, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(cb.equal(root.get("success"), true));
            if (!query.isEmpty()) {
                String filter = "%" + query.toLowerCase() + "%";
                predicateList.add(
                        cb.or(
                                cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName")), filter),
                                cb.like(cb.lower(root.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName")), filter)
                        ));
            }


            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };
    }

}
