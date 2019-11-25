package com.travelinsurancemaster.services.specifications;

import com.travelinsurancemaster.model.dto.AffiliateCommission;
import com.travelinsurancemaster.model.security.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;;
import java.util.List;

public class UsersSpecification {
  public static Specification<User> findByEmailOrName(final String query) {
    return getUserSpecification(query, false);
  }

  public static Specification<User> findAffiliateByEmailOrName(final String query) {
    return getUserSpecification(query, true);
  }

  private static Specification<User> getUserSpecification(String query, boolean isAffiliate) {
    return (root, criteriaQuery, cb) -> {
      List<Predicate> predicateList = new ArrayList<>();
      predicateList.add(cb.isNull(root.get("deleted")));
      if (!query.isEmpty()) {
        String filter = "%" + query.toLowerCase() + "%";
        Expression<String> exp1 = cb.concat(root.get("name"), " ");
        exp1 = cb.concat(exp1, root.get("userInfo").get("lastName"));
        predicateList.add(
            cb.or(
                cb.like(cb.lower(root.get("email")), filter),
                cb.like(cb.lower(exp1), filter)));
      }
      if (isAffiliate) {
        Subquery<Long> affiliatesSubQuery = criteriaQuery.subquery(Long.class);
        Root<AffiliateCommission> affiliateCommissionRoot =
            affiliatesSubQuery.from(AffiliateCommission.class);
        affiliatesSubQuery.select(affiliateCommissionRoot.get("affiliate")).distinct(true);

        predicateList.add(root.get("id").in(affiliatesSubQuery));
      }

      return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
    };
  }
}
