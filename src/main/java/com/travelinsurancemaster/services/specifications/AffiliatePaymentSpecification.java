package com.travelinsurancemaster.services.specifications;

import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.purchase.AffiliatePaymentFilter;
import com.travelinsurancemaster.model.dto.purchase.Purchase;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.util.BigDecimalRange;
import com.travelinsurancemaster.model.util.DateRange;
import com.travelinsurancemaster.model.util.IntegerRange;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ritchie on 5/26/15.
 */
@SuppressWarnings("ALL")
public class AffiliatePaymentSpecification {

    public static Specification<AffiliatePayment> doAffiliatePaymentFilter(final AffiliatePaymentFilter filter) {
        return new Specification<AffiliatePayment>() {
            @Override
            public Predicate toPredicate(Root<AffiliatePayment> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                //Purchase filter subquery
                Subquery purchaseSubquery = query.subquery(Long.class);
                Root purchaseRoot = purchaseSubquery.from(Purchase.class);

                Join<AffiliateCommission, Purchase> affiliateCommissionRoot = purchaseRoot.join("affiliateCommission", JoinType.LEFT);
                Join<User, AffiliatePayment> userRoot = root.join("affiliateUser",  JoinType.LEFT);
                Join<UserInfo, User> userInfoRoot = userRoot.join("userInfo",  JoinType.LEFT);

                List<Predicate> purchasePredicateList = new ArrayList<>();
                purchasePredicateList.add(cb.equal(purchaseRoot.get("success"), true));
                purchasePredicateList.add(cb.equal(purchaseRoot.get("purchaseType"), Purchase.PurchaseType.REAL));
                if (CollectionUtils.isNotEmpty(filter.getUsers())) {
                    purchasePredicateList.add(purchaseRoot.<User>get("user").in(filter.getUsers()));
                }
                if (CollectionUtils.isNotEmpty(filter.getVendors())) {
                    purchasePredicateList.add(purchaseRoot.<Vendor>get("policyMeta").get("vendor").in(filter.getVendors()));
                }
                if (CollectionUtils.isNotEmpty(filter.getPolicies())) {
                    purchasePredicateList.add(purchaseRoot.<PolicyMeta>get("policyMeta").in(filter.getPolicies()));
                }
                DateRange departDate = filter.getDepartDate();
                if (departDate.getFrom() != null && departDate.getTo() != null) {
                    purchasePredicateList.add(cb.between(purchaseRoot.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getFrom(), departDate.getTo()));
                } else if (departDate.getTo() == null && departDate.getFrom() != null) {
                    purchasePredicateList.add(cb.greaterThanOrEqualTo(purchaseRoot.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getFrom()));
                } else if (departDate.getTo() != null) {
                    purchasePredicateList.add(cb.lessThanOrEqualTo(purchaseRoot.<Date>get("purchaseQuoteRequest").get("departDate"), departDate.getTo()));
                }
                DateRange purchaseDate = filter.getPurchaseDate();
                if (purchaseDate.getFrom() != null && purchaseDate.getTo() != null) {
                    purchasePredicateList.add(cb.between(purchaseRoot.<Date>get("purchaseDate"), purchaseDate.getFrom(), purchaseDate.getTo()));
                } else if (purchaseDate.getTo() == null && purchaseDate.getFrom() != null) {
                    purchasePredicateList.add(cb.greaterThanOrEqualTo(purchaseRoot.<Date>get("purchaseDate"), purchaseDate.getFrom()));
                } else if (purchaseDate.getTo() != null) {
                    purchasePredicateList.add(cb.lessThanOrEqualTo(purchaseRoot.<Date>get("purchaseDate"), purchaseDate.getTo()));
                }
                BigDecimalRange tripCost = filter.getTripCost();
                if (tripCost.getFrom() != null && tripCost.getTo() != null) {
                    purchasePredicateList.add(cb.between(purchaseRoot.<BigDecimal>get("purchaseQuoteRequest").get("tripCost"), tripCost.getFrom(), tripCost.getTo()));
                } else if (tripCost.getTo() == null && tripCost.getFrom() != null) {
                    purchasePredicateList.add(cb.greaterThanOrEqualTo(purchaseRoot.<BigDecimal>get("purchaseQuoteRequest").get("tripCost"), tripCost.getFrom()));
                } else if (tripCost.getTo() != null) {
                    purchasePredicateList.add(cb.lessThanOrEqualTo(purchaseRoot.<BigDecimal>get("purchaseQuoteRequest").get("tripCost"), tripCost.getTo()));
                }
                BigDecimalRange policyPrice = filter.getPolicyPrice();
                if (policyPrice.getFrom() != null && policyPrice.getTo() != null) {
                    purchasePredicateList.add(cb.between(purchaseRoot.<BigDecimal>get("totalPrice"), policyPrice.getFrom(), policyPrice.getTo()));
                } else if (policyPrice.getTo() == null && policyPrice.getFrom() != null) {
                    purchasePredicateList.add(cb.greaterThanOrEqualTo(purchaseRoot.<BigDecimal>get("totalPrice"), policyPrice.getFrom()));
                } else if (policyPrice.getTo() != null) {
                    purchasePredicateList.add(cb.lessThanOrEqualTo(purchaseRoot.<BigDecimal>get("totalPrice"), policyPrice.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getPolicyNumber())) {
                    String policyNumber = "%" + filter.getPolicyNumber().trim().toLowerCase() + "%";
                    purchasePredicateList.add(cb.like(cb.lower(purchaseRoot.<String>get("policyNumber")), policyNumber));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getFirstName())) {
                    String firstName = "%" + filter.getTraveler().getFirstName().trim().toLowerCase() + "%";
                    purchasePredicateList.add(cb.like(cb.lower(purchaseRoot.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName")), firstName));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getMiddleInitials())) {
                    String middleInitials = filter.getTraveler().getMiddleInitials().trim().toLowerCase() + "%";
                    purchasePredicateList.add(cb.like(cb.lower(purchaseRoot.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("middleInitials")), middleInitials));
                }
                if (StringUtils.isNotBlank(filter.getTraveler().getLastName())) {
                    String lastName = "%" + filter.getTraveler().getLastName().trim().toLowerCase() + "%";
                    purchasePredicateList.add(cb.like(cb.lower(purchaseRoot.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName")), lastName));
                }
                IntegerRange age = filter.getTraveler().getAge();
                if (age.getFrom() != null && age.getTo() != null) {
                    purchasePredicateList.add(cb.between(purchaseRoot.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getFrom(), age.getTo()));
                } else if (age.getTo() == null && age.getFrom() != null) {
                    purchasePredicateList.add(cb.greaterThanOrEqualTo(purchaseRoot.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getFrom()));
                } else if (age.getTo() != null) {
                    purchasePredicateList.add(cb.lessThanOrEqualTo(purchaseRoot.<Integer>get("purchaseQuoteRequest").get("primaryTraveler").get("age"), age.getTo()));
                }
                if (StringUtils.isNotBlank(filter.getSearchKeyword())) {
                    String keyword = "%" + filter.getSearchKeyword().trim().toLowerCase() + "%";
                    Expression<String> exp1 = cb.concat(userRoot.get("name"), " ");
                    exp1 = cb.concat(exp1, userInfoRoot.get("lastName"));

                    Expression<String> exp2 = cb.concat(purchaseRoot.<String>get("user").get("name"), " ");
                    exp2 = cb.concat(exp2, purchaseRoot.<String>get("user").get("userInfo").get("lastName"));

                    Expression<String> exp3 = cb.concat(purchaseRoot.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("firstName"), " ");
                    exp3 = cb.concat(exp3, purchaseRoot.<String>get("purchaseQuoteRequest").get("primaryTraveler").get("lastName"));
                    purchasePredicateList.add(cb.or(
                            cb.like(cb.lower(exp1), keyword),
                            cb.like(cb.lower(exp2), keyword),
                            cb.like(cb.lower(exp3), keyword),
                            cb.like(cb.lower(purchaseRoot.<String>get("policyMeta").get("vendor").get("name")), keyword),
                            cb.like(cb.lower(purchaseRoot.<String>get("policyNumber")), keyword),
                            cb.like(purchaseRoot.<Date>get("purchaseDate").as(String.class), keyword),
                            cb.like(purchaseRoot.<BigDecimal>get("totalPrice").as(String.class), keyword),
                            cb.like(cb.lower(exp3), keyword),
                            cb.like(purchaseRoot.<BigDecimal>get("purchaseQuoteRequest").get("tripCost").as(String.class), keyword),
                            cb.like(purchaseRoot.<Date>get("purchaseQuoteRequest").get("departDate").as(String.class), keyword),
                            cb.like(cb.lower(purchaseRoot.<String>get("note")), keyword)
                    ));
                }

                Predicate purchasePredicate = cb.and(purchasePredicateList.toArray(new Predicate[purchasePredicateList.size()]));
                purchaseSubquery.select(cb.count(purchaseRoot.get("id"))).where(cb.and(cb.equal(affiliateCommissionRoot.get("affiliatePayment"), root), purchasePredicate));

                //Salary correction filter subquery
                Subquery salaryCorrectionSubquery = query.subquery(Long.class);
                Root salaryCorrectionRoot = salaryCorrectionSubquery.from(SalaryCorrection.class);
                salaryCorrectionSubquery.select(cb.count(salaryCorrectionRoot.get("id"))).where(cb.equal(salaryCorrectionRoot.get("affiliatePayment"), root));
                
                //Main affiliate payment filter query
                List<Predicate> affiliatePaymentPredicateList = new ArrayList<>();
                //Filter by affiliate users
                if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                    affiliatePaymentPredicateList.add(root.<User>get("affiliateUser").in(filter.getAffiliates()));
                }
                //Filter by affiliate companies
                if (CollectionUtils.isNotEmpty(filter.getCompanies())) {
                    affiliatePaymentPredicateList.add(root.<User>get("affiliateCompany").in(filter.getCompanies()));
                }
                //Payment date range
                DateRange paymentDate = filter.getPaymentDate();
                if(paymentDate.getFrom() != null && paymentDate.getTo() != null) {
                    affiliatePaymentPredicateList.add(cb.between(root.<Date>get("paymentDate"), paymentDate.getFrom(), paymentDate.getTo()));
                } else if (paymentDate.getTo() == null && paymentDate.getFrom() != null) {
                    affiliatePaymentPredicateList.add(cb.greaterThanOrEqualTo(root.<Date>get("paymentDate"), paymentDate.getFrom()));
                } else if (paymentDate.getTo() != null) {
                    affiliatePaymentPredicateList.add(cb.lessThanOrEqualTo(root.<Date>get("paymentDate"), paymentDate.getTo()));
                }
                //Payment total range
                BigDecimalRange paymentTotal = filter.getPaymentTotal();
                if(paymentTotal.getFrom() != null && paymentTotal.getTo() != null) {
                    affiliatePaymentPredicateList.add(cb.between(root.<BigDecimal>get("total"), paymentTotal.getFrom(), paymentTotal.getTo()));
                } else if (paymentTotal.getTo() == null && paymentTotal.getFrom() != null) {
                    affiliatePaymentPredicateList.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("total"), paymentTotal.getFrom()));
                } else if (paymentTotal.getTo() != null) {
                    affiliatePaymentPredicateList.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("total"), paymentTotal.getTo()));
                }

                //Combine all predicates
                Predicate affiliatePaymentPredicate = cb.and(affiliatePaymentPredicateList.toArray(new Predicate[affiliatePaymentPredicateList.size()]));

                Predicate predicate;
                if(!filter.isPurchaseFilterEmpty()) {
                    predicate = cb.and(affiliatePaymentPredicate, cb.greaterThan(purchaseSubquery, 0));
                } else {
                    predicate = cb.and(affiliatePaymentPredicate, cb.or(cb.greaterThan(purchaseSubquery, 0), cb.greaterThan(salaryCorrectionSubquery, 0)));
                }
                return predicate;
            }
        };
    }
}