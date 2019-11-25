package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.PolicyMetaCategoryRestriction;
import com.travelinsurancemaster.repository.PolicyMetaCategoryRestrictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ritchie on 7/26/16.
 */
@Service
@Transactional
public class PolicyMetaCategoryRestrictionService {

    @Autowired
    private PolicyMetaCategoryRestrictionRepository policyMetaCategoryRestrictionRepository;

    @Transactional(readOnly = true)
    public List<PolicyMetaCategoryRestriction> getPolicyMetaCategoryRestrictions(Long policyMetaCategoryId) {
        return policyMetaCategoryRestrictionRepository.findByPolicyMetaCategoryId(policyMetaCategoryId);
    }
}
