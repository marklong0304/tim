package com.travelinsurancemaster.services;

import com.travelinsurancemaster.CacheConfig;
import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.PolicyQuoteParam;
import com.travelinsurancemaster.model.dto.PolicyQuoteParamValue;
import com.travelinsurancemaster.model.webservice.common.QuoteRequest;
import com.travelinsurancemaster.repository.PolicyQuoteParamRepository;
import com.travelinsurancemaster.repository.PolicyQuoteParamRestrictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by ritchie on 4/13/15.
 */
@Service
@Transactional
public class PolicyQuoteParamService {

    @Autowired
    private PolicyQuoteParamRepository policyQuoteParamRepository;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private PolicyQuoteParamRestrictionRepository policyQuoteParamRestrictionRepository;

    /**
     * @return policy quote param depending on restrictions.
     */
    @Transactional(readOnly = true)
    public Long getPolicyQuoteParam(QuoteRequest quoteRequest, PolicyMeta policyMeta, Long val1, Long val2, Long val3, Long val4, Long val5) {
        List<PolicyQuoteParam> params = policyQuoteParamRepository.findByPolicyMetaId(policyMeta.getId());
        if (params != null) {
            for (PolicyQuoteParam param : params) {
                if (restrictionService.isValid(quoteRequest, param.getPolicyQuoteParamRestrictions()) && isEqual(param, val1, val2, val3, val4, val5)) {
                    return param.getValue();
                }
            }
        }
        return null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private boolean isEqual(PolicyQuoteParam param, Long val1, Long val2, Long val3, Long val4, Long val5) {
        //both param and val should be null or not null
        boolean param1Res = param.getParam1() == null ? val1 == null : val1 != null && isEqual(param.getParam1(), val1);
        boolean param2Res = param.getParam2() == null ? val2 == null : val2 != null && isEqual(param.getParam2(), val2);
        boolean param3Res = param.getParam3() == null ? val3 == null : val3 != null && isEqual(param.getParam3(), val3);
        boolean param4Res = param.getParam4() == null ? val4 == null : val4 != null && isEqual(param.getParam4(), val4);
        boolean param5Res = param.getParam5() == null ? val5 == null : val5 != null && isEqual(param.getParam5(), val5);
        return param1Res && param2Res && param3Res && param4Res && param5Res;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private boolean isEqual(PolicyQuoteParamValue param, Long value) {
        switch (param.getType()) {
            case SIMPLE:
                return param.getValueFrom().equals(value);
            case RANGE:
                return param.getValueFrom() <= value && value <= param.getValueTo();
        }
        return false;
    }

    @Transactional(readOnly = true)
    public PolicyQuoteParam getById(Long quoteParamId) {
        return this.policyQuoteParamRepository.findOne(quoteParamId);
    }

    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public PolicyQuoteParam save(PolicyQuoteParam quoteParamUpd) {
        PolicyQuoteParam quoteParam;
        if (quoteParamUpd.getId() != null && (quoteParam = policyQuoteParamRepository.findOne(quoteParamUpd.getId())) != null) {
            quoteParam = populate(quoteParamUpd, quoteParam);
            prepareQuoteParam(quoteParam);
            return policyQuoteParamRepository.saveAndFlush(quoteParam);
        }
        prepareQuoteParam(quoteParamUpd);
        return this.policyQuoteParamRepository.saveAndFlush(quoteParamUpd);
    }

    private PolicyQuoteParam populate(PolicyQuoteParam quoteParamUpd, PolicyQuoteParam quoteParam) {
        quoteParam.setParam1(quoteParamUpd.getParam1());
        quoteParam.setParam2(quoteParamUpd.getParam2());
        quoteParam.setParam3(quoteParamUpd.getParam3());
        quoteParam.setParam4(quoteParamUpd.getParam4());
        quoteParam.setParam5(quoteParamUpd.getParam5());
        quoteParam.setPolicyMeta(quoteParamUpd.getPolicyMeta());
        quoteParam.setValue(quoteParamUpd.getValue());
        quoteParam.setPolicyQuoteParamRestrictions(quoteParam.getPolicyQuoteParamRestrictions());
        return quoteParam;
    }

    private void prepareQuoteParam(PolicyQuoteParam quoteParam) {
        if (quoteParam.getParam1() != null && quoteParam.getParam1().getType() == PolicyQuoteParamValue.QuoteParamType.NONE)
            quoteParam.setParam1(null);
        if (quoteParam.getParam2() != null && quoteParam.getParam2().getType() == PolicyQuoteParamValue.QuoteParamType.NONE)
            quoteParam.setParam2(null);
        if (quoteParam.getParam3() != null && quoteParam.getParam3().getType() == PolicyQuoteParamValue.QuoteParamType.NONE)
            quoteParam.setParam3(null);
        if (quoteParam.getParam4() != null && quoteParam.getParam4().getType() == PolicyQuoteParamValue.QuoteParamType.NONE)
            quoteParam.setParam4(null);
        if (quoteParam.getParam5() != null && quoteParam.getParam5().getType() == PolicyQuoteParamValue.QuoteParamType.NONE)
            quoteParam.setParam5(null);
    }

    @CacheEvict(value = {CacheConfig.POLICY_META_CACHE}, allEntries = true)
    public void delete(PolicyQuoteParam quoteParam) {
        this.policyQuoteParamRepository.delete(quoteParam);
    }
}
