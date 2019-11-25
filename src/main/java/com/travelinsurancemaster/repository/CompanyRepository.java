package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByDeletedNull();

    List<Company> findByNameLikeIgnoreCaseAndDeletedNullOrderByName(String filter);
}
