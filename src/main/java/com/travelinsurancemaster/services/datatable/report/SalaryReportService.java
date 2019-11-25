package com.travelinsurancemaster.services.datatable.report;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.SalaryCorrection;
import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.dto.json.datatable.salary.report.SalaryCorrectionJson;
import com.travelinsurancemaster.model.dto.json.datatable.salary.report.SalaryReportDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.json.datatable.salary.report.SalaryReportJson;
import com.travelinsurancemaster.model.dto.purchase.SalaryFilter;
import com.travelinsurancemaster.model.dto.report.SalaryReportFilter;
import com.travelinsurancemaster.model.security.Role;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.model.util.BigDecimalRange;
import com.travelinsurancemaster.model.util.DateRange;
import com.travelinsurancemaster.repository.SalaryCorrectionRepository;
import com.travelinsurancemaster.services.PolicyMetaService;
import com.travelinsurancemaster.services.PurchaseService;
import com.travelinsurancemaster.services.VendorService;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.util.SecurityHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Chernov Artur on 29.09.15.
 */

@Service
public class SalaryReportService {
    private static final Logger log = LoggerFactory.getLogger(SalaryReportService.class);

    @Autowired
    private UserService userService;

    @Autowired private VendorService vendorService;

    @Autowired private PurchaseService purchaseService;

    @Autowired private PolicyMetaService policyMetaService;

    @Autowired
    private SalaryCorrectionRepository salaryCorrectionRepository;

    public SalaryCorrection getSalaryCorrection(String id) {
        SalaryCorrection salaryCorrection = null;
        Long salaryCorrectionId = null;
        try {
            salaryCorrectionId = Long.parseLong(id);
            if(salaryCorrectionId > 0) {
                salaryCorrection = salaryCorrectionRepository.findById(salaryCorrectionId);
            }
        } catch (Exception e) {}
        return salaryCorrection;
    }

    public SalaryCorrection getSalaryCorrection(Long id) {
        SalaryCorrection salaryCorrection = null;
        try {
            salaryCorrection = salaryCorrectionRepository.findById(id);
        } catch (Exception e) {}
        return salaryCorrection;
    }

    public void save(SalaryCorrection salaryCorrection) {
        salaryCorrectionRepository.save(salaryCorrection);
    }

    public void delete(Long id) { salaryCorrectionRepository.delete(id); }

    public List<SalaryCorrection> getAllByAffiliateId(long userId) {
        return salaryCorrectionRepository.findAllByAffiliateId(userId);
    }

    public SalaryReportFilter getSalaryReportFilter(SalaryReportDataTableJsonRequest request) {
        SalaryReportFilter salaryReportFilter = new SalaryReportFilter();
        for (Long userId : CollectionUtils.emptyIfNull(request.getAffiliates())) {
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                salaryReportFilter.getAffiliates().add(user.get());
            }
        }
        for (Long vendorId : CollectionUtils.emptyIfNull(request.getVendors())) {
            Optional<Vendor> vendor = Optional.ofNullable(vendorService.getById(vendorId));
            if (vendor.isPresent()) {
                salaryReportFilter.getVendors().add(vendor.get());
            }
        }
        for (Long policyId : CollectionUtils.emptyIfNull(request.getPolicies())) {
            Optional<PolicyMeta> policy = Optional.ofNullable(policyMetaService.getCached(policyId));
            if (policy.isPresent()) {
                salaryReportFilter.getPolicies().add(policy.get());
            }
        }
        salaryReportFilter.setPurchaseDate(
                request.getPurchaseDate().getFrom(), request.getPurchaseDate().getTo());
        salaryReportFilter.setPolicyNumber(request.getPolicyNumber());
        salaryReportFilter.setNote(request.getNote());
        salaryReportFilter.setTraveler(request.getTraveler());
        if (!request.getSearch().getValue().isEmpty()) {
            salaryReportFilter.setSearchKeyword(request.getSearch().getValue());
        }
        salaryReportFilter.setPay(request.isPay());
        salaryReportFilter.setExpectedSalary(
                request.getExpectedSalary().getFrom(), request.getExpectedSalary().getTo());
        salaryReportFilter.setTotalPrice(request.getTotalPrice().getFrom(), request.getTotalPrice().getTo());
        salaryReportFilter.setSalary(request.getSalary().getFrom(), request.getSalary().getTo());
        salaryReportFilter.setCancellation(request.isCancellation());
        return salaryReportFilter;
    }

    public void addSalaryCorrection(SalaryCorrectionJson salaryCorrectionJson){
        User affiliate = userService.get(Long.parseLong(salaryCorrectionJson.getAffiliateId()));
        if (affiliate == null) {
            throw new IllegalArgumentException("Wrong affiliate id provided");
        }

        salaryCorrectionRepository.save(new SalaryCorrection(salaryCorrectionJson, affiliate));
    }

    public List<SalaryCorrection> getSalaryCorrection(SalaryFilter filter){

        if (CollectionUtils.isNotEmpty(filter.getVendors()) || CollectionUtils.isNotEmpty(filter.getPolicies())
                || StringUtils.isNotBlank(filter.getPolicyNumber()) || StringUtils.isNotBlank(filter.getTraveler().getFirstName())
                || filter.getExpectedSalary().getFrom() != null || filter.getExpectedSalary().getTo() != null
                || filter.getTotalPrice().getFrom() != null || filter.getTotalPrice().getTo() != null
                || filter.getReceivedCommission().getFrom() != null || filter.getReceivedCommission().getTo() != null
                || filter.isCancellation() != null && filter.isCancellation())
            return Collections.emptyList();

        Specification<SalaryCorrection> specification = (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                predicateList.add(root.get("affiliate").in(filter.getAffiliates()));
            }

            DateRange purchaseDate = filter.getPurchaseDate();
            if (purchaseDate.getFrom() != null && purchaseDate.getTo() != null) {
                predicateList.add(cb.between(root.get("receivedDate"), purchaseDate.getFrom(), purchaseDate.getTo()));
            } else if (purchaseDate.getTo() == null && purchaseDate.getFrom() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(root.get("receivedDate"), purchaseDate.getFrom()));
            } else if (purchaseDate.getTo() != null) {
                predicateList.add(cb.lessThanOrEqualTo(root.get("receivedDate"), purchaseDate.getTo()));
            }

            BigDecimalRange salary = filter.getSalary();
            if (salary.getFrom()!= null && salary.getTo() != null) {
                predicateList.add(cb.between(root.get("salary"), salary.getFrom(), salary.getTo()));
            } else if (salary.getTo() == null && salary.getFrom() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(root.get("salary"), salary.getFrom()));
            } else if (salary.getTo() != null) {
                predicateList.add(cb.lessThanOrEqualTo(root.get("salary"), salary.getTo()));
            }
            if (filter.isPay() != null) {
                predicateList.add(filter.isPay() ? cb.isNotNull(root.get("paid")) : cb.isNull(root.get("paid")));
            }
            if (StringUtils.isNotBlank(filter.getSearchKeyword())) {
                String keyword = "%" + filter.getSearchKeyword().trim().toLowerCase() + "%";
                Expression<String> exp1 = cb.concat(root.<String>get("affiliate").get("name"), " ");
                exp1 = cb.concat(exp1, root.<String>get("affiliate").get("userInfo").get("lastName"));

                predicateList.add(cb.or(
                        cb.like(cb.lower(exp1), keyword),
                        cb.like(root.<BigDecimal>get("salary").as(String.class), keyword),
                        cb.like(cb.lower(root.get("note")), keyword)
                ));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };

        return salaryCorrectionRepository.findAll(specification);
    }

    public Page<SalaryCorrection> getSalaryCorrectionBySalarySearchFilter(SalaryFilter filter, Pageable pageable){
        Specification<SalaryCorrection> specification = (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(cb.isNotNull((root.get("paid"))));

            if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                predicateList.add(root.get("affiliate").in(filter.getAffiliates()));
            }

            DateRange payDate = filter.getPayDate();
            if (payDate.getFrom() != null && payDate.getTo() != null) {
                predicateList.add(cb.between(root.get("paid"), payDate.getFrom(), payDate.getTo()));
            } else if (payDate.getTo() == null && payDate.getFrom() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(root.get("paid"), payDate.getFrom()));
            } else if (payDate.getTo() != null) {
                predicateList.add(cb.lessThanOrEqualTo(root.get("paid"), payDate.getTo()));
            }

            BigDecimalRange salary = filter.getSalary();
            if (salary.getFrom()!= null && salary.getTo() != null) {
                predicateList.add(cb.between(root.get("salary"), salary.getFrom(), salary.getTo()));
            } else if (salary.getTo() == null && salary.getFrom() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(root.get("salary"), salary.getFrom()));
            } else if (salary.getTo() != null) {
                predicateList.add(cb.lessThanOrEqualTo(root.get("salary"), salary.getTo()));
            }

            if (StringUtils.isNotBlank(filter.getSearchKeyword())) {
                String keyword = "%" + filter.getSearchKeyword().trim().toLowerCase() + "%";
                Expression<String> exp1 = cb.concat(root.<String>get("affiliate").get("name"), " ");
                exp1 = cb.concat(exp1, root.<String>get("affiliate").get("userInfo").get("lastName"));

                predicateList.add(cb.or(
                        cb.like(cb.lower(exp1), keyword),
                        cb.like(root.<BigDecimal>get("salary").as(String.class), keyword)
                ));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };

        return salaryCorrectionRepository.findAll(specification, pageable);
    }

    public List<SalaryCorrection> getSalaryCorrection(SalaryReportFilter filter){
        if (CollectionUtils.isNotEmpty(filter.getVendors()) || CollectionUtils.isNotEmpty(filter.getPolicies())
                || StringUtils.isNotBlank(filter.getPolicyNumber()) || StringUtils.isNotBlank(filter.getTraveler().getFirstName())
                || filter.getExpectedSalary().getFrom() != null || filter.getExpectedSalary().getTo() != null
                || filter.getTotalPrice().getFrom() != null || filter.getTotalPrice().getTo() != null
                || filter.isCancellation() != null && filter.isCancellation())
            return Collections.emptyList();

        Specification<SalaryCorrection> specification = (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(filter.getAffiliates())) {
                predicateList.add(root.get("affiliate").in(filter.getAffiliates()));
            }

            DateRange purchaseDate = filter.getPurchaseDate();
            if (purchaseDate.getFrom() != null && purchaseDate.getTo() != null) {
                predicateList.add(cb.between(root.get("receivedDate"), purchaseDate.getFrom(), purchaseDate.getTo()));
            } else if (purchaseDate.getTo() == null && purchaseDate.getFrom() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(root.get("receivedDate"), purchaseDate.getFrom()));
            } else if (purchaseDate.getTo() != null) {
                predicateList.add(cb.lessThanOrEqualTo(root.get("receivedDate"), purchaseDate.getTo()));
            }

            BigDecimalRange salary = filter.getSalary();
            if (salary.getFrom()!= null && salary.getTo() != null) {
                predicateList.add(cb.between(root.get("salary"), salary.getFrom(), salary.getTo()));
            } else if (salary.getTo() == null && salary.getFrom() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(root.get("salary"), salary.getFrom()));
            } else if (salary.getTo() != null) {
                predicateList.add(cb.lessThanOrEqualTo(root.get("salary"), salary.getTo()));
            }
            if (filter.isPay() != null) {
                predicateList.add(filter.isPay() ? cb.isNotNull(root.get("paid")) : cb.isNull(root.get("paid")));
            }
            if (StringUtils.isNotBlank(filter.getSearchKeyword())) {
                String keyword = "%" + filter.getSearchKeyword().trim().toLowerCase() + "%";
                Expression<String> exp1 = cb.concat(root.<String>get("affiliate").get("name"), " ");
                exp1 = cb.concat(exp1, root.<String>get("affiliate").get("userInfo").get("lastName"));

                predicateList.add(cb.or(
                        cb.like(cb.lower(exp1), keyword),
                        cb.like(root.<BigDecimal>get("salary").as(String.class), keyword),
                        cb.like(cb.lower(root.get("note")), keyword)
                ));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };

        return salaryCorrectionRepository.findAll(specification);
    }

    public void fillEditable(SalaryReportJson salaryReportJson) {
        salaryReportJson.setEditable((hasEditablePermissions()));
    }

    private boolean hasEditablePermissions() {
        User currentUser = SecurityHelper.getCurrentUser();
        return currentUser != null && (currentUser.hasRole(Role.ROLE_ACCOUNTANT) || currentUser.hasRole(Role.ROLE_ADMIN));
    }
}
