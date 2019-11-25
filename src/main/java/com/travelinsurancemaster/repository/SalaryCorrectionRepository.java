package com.travelinsurancemaster.repository;


import com.travelinsurancemaster.model.dto.SalaryCorrection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryCorrectionRepository extends JpaRepository<SalaryCorrection, Long>, JpaSpecificationExecutor<SalaryCorrection> {

   SalaryCorrection findById(Long id);

   List<SalaryCorrection> findAllByAffiliateId(Long id);
}