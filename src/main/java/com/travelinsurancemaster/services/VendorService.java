package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.Vendor;
import com.travelinsurancemaster.model.webservice.common.CardType;
import com.travelinsurancemaster.repository.VendorRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by ritchie on 5/26/15.
 */

@Service
@Transactional
public class VendorService {

    private static final List<String> vendorsWithoutCarRentalDates = Arrays.asList("Trawick", "ITravelInsured", "RoamRight");

    private final VendorRepository vendorRepository;

    private final AwsService awsService;

    public VendorService(VendorRepository vendorRepository, AwsService awsService) {
        this.vendorRepository = vendorRepository;
        this.awsService = awsService;
    }

    public List<Vendor> findAllActiveSortedByName() {
        return vendorRepository.findByActiveTrueAndTestFalseAndDeletedDateIsNull(sortByNameAscIgnoreCase());
    }

    public List<Vendor> findAllSortedByName() {
        return vendorRepository.findAllByDeletedDateIsNull(sortByNameAscIgnoreCase());
    }

    public List<Vendor> findAllSortedByNameAndCanDelete() {
        return findAllSortedByName();
    }

    private Sort sortByNameAscIgnoreCase() {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
        return new Sort(order);
    }

    public Vendor getById(Long id) {
        Vendor vendor = vendorRepository.findOne(id);
        if (vendor == null) {
            return null;
        }
        Hibernate.initialize(vendor.getUnsupportedCardTypes());
        return vendor;
    }

    public Vendor getByCode(String code) {
        Vendor vendor = vendorRepository.findByCodeAndDeletedDateIsNull(code);
        if (vendor == null) {
            return null;
        }
        Hibernate.initialize(vendor.getUnsupportedCardTypes());
        return vendor;
    }

    public Vendor getByName(String name) {
        Vendor vendor = vendorRepository.findByName(name);
        if (vendor == null) {
            return null;
        }
        Hibernate.initialize(vendor.getUnsupportedCardTypes());
        return vendor;
    }

    public Vendor save(Vendor vendorUpd) {
        Vendor vendor;
        if (vendorUpd.getId() != null && (vendor = vendorRepository.findOne(vendorUpd.getId())) != null) {
            vendor.setName(vendorUpd.getName());
            vendor.setCode(vendorUpd.getCode());
            vendor.setPercentType(vendorUpd.getPercentType());
            vendor.setPercentInfo(vendorUpd.getPercentInfo());
            vendor.getUnsupportedCardTypes().clear();
            if (CollectionUtils.isNotEmpty(vendorUpd.getUnsupportedCardTypes())) {
                vendor.getUnsupportedCardTypes().addAll(vendorUpd.getUnsupportedCardTypes());
            }
            vendor.setActive(vendorUpd.isActive());
            vendor.setAgeFromDepartDate(vendorUpd.isAgeFromDepartDate());
            vendor.setBeneficiaryType(vendorUpd.getBeneficiaryType());
            vendor.setTermsAndConditionsIsActive(vendorUpd.isTermsAndConditionsIsActive());
            vendor.setTermsAndConditionsType(vendorUpd.getTermsAndConditionsType());
            vendor.setTermsAndConditionsText(vendorUpd.getTermsAndConditionsText().trim());
            vendor.setTest(vendorUpd.isTest());
            vendor.setTestUserIds(vendorUpd.getTestUserIds());
            vendor.setShowPureConsumers(vendorUpd.isShowPureConsumers());
            return vendorRepository.save(vendor);
        }
        return vendorRepository.saveAndFlush(vendorUpd);
    }

    public Vendor getVendorWithLogo(String vendorCode) {
        if (StringUtils.isEmpty(vendorCode)) {
            return null;
        }
        Vendor vendor = getByCode(vendorCode);
        if (vendor != null) {
            Hibernate.initialize(vendor.getIcon());
        }
        return vendor;
    }

    public Vendor getVendorWithLogo(Long vendorId) {
        Vendor vendor = getById(vendorId);
        if (vendor != null) {
            Hibernate.initialize(vendor.getIcon());
        }
        return vendor;
    }

    public Vendor updateLogo(Long id, byte[] bytes) {
        if (id == null || bytes == null) {
            return null;
        }
        Vendor vendor = vendorRepository.findOne(id);
        if (vendor == null) {
            return null;
        }
        vendor.setIcon(bytes);
        vendor.setIconLastModified(new Date());
        awsService.syncWithS3VendorLogo(bytes,vendor.getCode() + ".png");
        return vendorRepository.saveAndFlush(vendor);
    }

    public Set<CardType> getSupportedCreditCards(Long id) {
        Vendor vendor = getById(id);
        if (vendor == null || CollectionUtils.isEmpty(vendor.getUnsupportedCardTypes())) {
            return new TreeSet<>(Arrays.asList(CardType.values()));
        }
        Set<CardType> supportedCreditCards = new TreeSet<>();
        for (CardType cardType : CardType.values()) {
            if (!vendor.getUnsupportedCardTypes().contains(cardType)) {
                supportedCreditCards.add(cardType);
            }
        }
        return supportedCreditCards;
    }

    public boolean hasRentalCarDates(Vendor vendor) {
        return !vendorsWithoutCarRentalDates.contains(vendor.getCode());
    }
}
