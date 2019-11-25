package com.travelinsurancemaster.repository;


import com.travelinsurancemaster.model.dto.SalaryCorrection;
import com.travelinsurancemaster.model.dto.ServiceParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceParamRepository extends JpaRepository<ServiceParam, Long>, JpaSpecificationExecutor<SalaryCorrection> {

   ServiceParam findByCode(String code);

}