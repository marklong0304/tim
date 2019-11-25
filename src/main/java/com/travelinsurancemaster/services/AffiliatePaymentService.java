package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.*;
import com.travelinsurancemaster.model.dto.json.datatable.affiliatePayment.AffiliatePaymentDataTableJsonRequest;
import com.travelinsurancemaster.model.dto.purchase.AffiliatePaymentFilter;
import com.travelinsurancemaster.model.dto.query.IAffiliateCompanyBalance;
import com.travelinsurancemaster.model.dto.query.IAffiliatePaymentData;
import com.travelinsurancemaster.model.dto.query.IAffiliateUserBalance;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.repository.AffiliateCommissionRepository;
import com.travelinsurancemaster.repository.AffiliatePaymentRepository;
import com.travelinsurancemaster.repository.SalaryCorrectionRepository;
import com.travelinsurancemaster.services.security.UserService;
import com.travelinsurancemaster.services.specifications.AffiliatePaymentSpecification;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Raman on 24.06.19.
 */

@Service
public class AffiliatePaymentService {

    @Autowired
    AffiliatePaymentRepository affiliatePaymentRepository;

    @Autowired
    AffiliateCommissionRepository affiliateCommissionRepository;

    @Autowired
    SalaryCorrectionRepository salaryCorrectionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private PolicyMetaService policyMetaService;

    public AffiliatePayment get(Long id) { return affiliatePaymentRepository.findOne(id); }

    public AffiliatePayment save(AffiliatePayment affiliatePayment) {
        return affiliatePaymentRepository.saveAndFlush(affiliatePayment);
    }

    public AffiliatePayment saveWithPayments(AffiliatePayment affiliatePayment) {

        BigDecimal total = BigDecimal.ZERO;

        for(AffiliateCommission affiliateCommission : affiliatePayment.getAffiliateCommissions()) {
            AffiliateCommission savedAffiliateCommission = affiliateCommissionRepository.getOne(affiliateCommission.getId());
            if(savedAffiliateCommission != null) {
                savedAffiliateCommission.setPaid(affiliateCommission.getPaid());
                savedAffiliateCommission.setSalaryPaid(affiliateCommission.getSalaryPaid());
                savedAffiliateCommission = affiliateCommissionRepository.saveAndFlush(savedAffiliateCommission);
                total = total.add(savedAffiliateCommission.getSalaryPaid());
            }
        }

        for(SalaryCorrection salaryCorrection : affiliatePayment.getSalaryCorrections()) {
            SalaryCorrection savedSalaryCorrection = salaryCorrectionRepository.getOne(salaryCorrection.getId());
            if(savedSalaryCorrection != null) {
                savedSalaryCorrection.setPaid(salaryCorrection.getPaid());
                savedSalaryCorrection.setSalaryPaid(salaryCorrection.getSalaryPaid());
                savedSalaryCorrection.setNote(salaryCorrection.getNote());
                savedSalaryCorrection = salaryCorrectionRepository.saveAndFlush(savedSalaryCorrection);
                total = total.add(savedSalaryCorrection.getSalaryPaid());
            }
        }

        affiliatePayment.setTotal(total);

        return affiliatePaymentRepository.saveAndFlush(affiliatePayment);
    }

    public AffiliatePayment updateTotal(long affiliatePaymentId) {
        BigDecimal total = BigDecimal.ZERO;
        AffiliatePayment affiliatePayment = get(affiliatePaymentId);
        for(AffiliateCommission affiliateCommission : affiliatePayment.getAffiliateCommissions()) {
            total = total.add(affiliateCommission.getSalaryPaid());
        };
        for(SalaryCorrection salaryCorrection : affiliatePayment.getSalaryCorrections()) {
            total = total.add(salaryCorrection.getSalaryPaid());
        }
        affiliatePayment.setTotal(total);
        return affiliatePaymentRepository.saveAndFlush(affiliatePayment);
    }

    public List<AffiliatePayment> getAllByAffiliatePaymentFilter(AffiliatePaymentFilter filter) {
        return affiliatePaymentRepository.findAll(AffiliatePaymentSpecification.doAffiliatePaymentFilter(filter));
    }

    public Page<AffiliatePayment> getAllByAffiliatePaymentFilter(AffiliatePaymentFilter filter, Pageable pageable) {
        return affiliatePaymentRepository.findAll(AffiliatePaymentSpecification.doAffiliatePaymentFilter(filter), pageable);
    }

    public AffiliatePaymentFilter getAffiliatePaymentFilter(AffiliatePaymentDataTableJsonRequest request) {
        AffiliatePaymentFilter affiliatePaymentFilter = new AffiliatePaymentFilter();
        for (Long userId : CollectionUtils.emptyIfNull(request.getAffiliates())) {
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                affiliatePaymentFilter.getAffiliates().add(user.get());
            }
        }
        for (Long companyId : CollectionUtils.emptyIfNull(request.getCompanies())) {
            Optional<Company> company = Optional.ofNullable(companyService.getById(companyId));
            if (company.isPresent()) {
                affiliatePaymentFilter.getCompanies().add(company.get());
            }
        }
        for (Long userId : CollectionUtils.emptyIfNull(request.getUsers())) {
            Optional<User> user = Optional.ofNullable(userService.get(userId));
            if (user.isPresent()) {
                affiliatePaymentFilter.getUsers().add(user.get());
            }
        }
        for (Long vendorId : CollectionUtils.emptyIfNull(request.getVendors())) {
            Optional<Vendor> vendor = Optional.ofNullable(vendorService.getById(vendorId));
            if (vendor.isPresent()) {
                affiliatePaymentFilter.getVendors().add(vendor.get());
            }
        }
        for (Long policyId : CollectionUtils.emptyIfNull(request.getPolicies())) {
            Optional<PolicyMeta> policy = Optional.ofNullable(policyMetaService.getPolicyMetaById(policyId));
            if (policy.isPresent()) {
                affiliatePaymentFilter.getPolicies().add(policy.get());
            }
        }
        affiliatePaymentFilter.setDepartDate(request.getDepartDate().getFrom(), request.getDepartDate().getTo());
        affiliatePaymentFilter.setPurchaseDate(request.getPurchaseDate().getFrom(), request.getPurchaseDate().getTo());
        affiliatePaymentFilter.setTripCost(request.getTripCost().getFrom(), request.getTripCost().getTo());
        affiliatePaymentFilter.setPolicyPrice(request.getPolicyPrice().getFrom(), request.getPolicyPrice().getTo());
        affiliatePaymentFilter.setPolicyNumber(request.getPolicyNumber());
        affiliatePaymentFilter.setTraveler(request.getTraveler());
        affiliatePaymentFilter.setPaymentDate(request.getPaymentDate().getFrom(), request.getPaymentDate().getTo());
        affiliatePaymentFilter.setPaymentTotal(request.getPaymentTotal().getFrom(), request.getPaymentTotal().getTo());
        if (!request.getSearch().getValue().isEmpty()) {
            affiliatePaymentFilter.setSearchKeyword(request.getSearch().getValue());
        }
        return affiliatePaymentFilter;
    }

    public AffiliatePayment updateAffiliatePaymentFromForm(AffiliatePayment affiliatePayment, AffiliatePayment affiliatePaymentForm) {

        //Update main fields
        affiliatePayment.setPaymentDate(affiliatePaymentForm.getPaymentDate());
        affiliatePayment.setPaymentOption(affiliatePaymentForm.getPaymentOption());
        affiliatePayment.setBankName(affiliatePaymentForm.getBankName());
        affiliatePayment.setBankRouting(affiliatePaymentForm.getBankRouting());
        affiliatePayment.setAccount(affiliatePaymentForm.getAccount());
        affiliatePayment.setPaypalEmailAddress(affiliatePaymentForm.getPaypalEmailAddress());
        affiliatePayment.setCheckNumber(affiliatePaymentForm.getCheckNumber());
        affiliatePayment.setStatusPaid(affiliatePaymentForm.getStatusPaid());

        //Update affiliate commissions
        updateAbsentSinglePayments(affiliatePayment.getAffiliateCommissions(), affiliatePaymentForm.getAffiliateCommissions());
        affiliatePayment.setAffiliateCommissions(affiliatePaymentForm.getAffiliateCommissions());

        //Update salary corrections
        updateAbsentSinglePayments(affiliatePayment.getSalaryCorrections(), affiliatePaymentForm.getSalaryCorrections());
        affiliatePayment.setSalaryCorrections(affiliatePaymentForm.getSalaryCorrections());

        return affiliatePayment;
    }

    private void updateAbsentSinglePayments(List<? extends IAffiliateSinglePayment> singlePayments, List<? extends IAffiliateSinglePayment> formSinglePayments) {
        formSinglePayments.removeIf(ac -> ac.getId() == null);
        List<Long> affiliateCommissionFormIds = formSinglePayments.stream().map(ac -> ac.getId()).collect(Collectors.toList());
        singlePayments.stream()
                .filter(sp -> !affiliateCommissionFormIds.contains(sp.getId()))
                .forEach(singlePayment -> {
                    singlePayment.setAffiliatePayment(null);
                    singlePayment.setPaid(null);
                    singlePayment.setSalaryPaid(null);
                });
    }

    public List<IAffiliateUserBalance> getAffiliateUserBalances() { return affiliatePaymentRepository.getAffiliateUserBalances(); }

    public List<IAffiliateCompanyBalance> getAffiliateCompanyBalances() {
        return affiliatePaymentRepository.getAffiliateCompanyBalances();
    }

    public List<IAffiliatePaymentData> getAffiliateUserPayments(Long affiliateUserId) { return affiliatePaymentRepository.getAffiliateUserPayments(affiliateUserId); }

    public List<IAffiliatePaymentData> getAffiliateCompanyPayments(Long affiliateCompanyId) { return affiliatePaymentRepository.getAffiliateCompanyPayments(affiliateCompanyId); }
}