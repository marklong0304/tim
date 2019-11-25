package com.travelinsurancemaster.services;

import com.travelinsurancemaster.model.dto.PolicyMeta;
import com.travelinsurancemaster.model.dto.ServiceParam;
import com.travelinsurancemaster.repository.PolicyMetaRepository;
import com.travelinsurancemaster.repository.ServiceParamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ritchie on 4/13/15.
 */

@Service
public class PolicyMetaCacheService {

    private static final String CACHED_TABLES_CHANED = "cachedTablesChanged";

    private static final Logger log = LoggerFactory.getLogger(PolicyMetaService.class);

    public final PolicyMetaService policyMetaService;

    private static Map<Long, PolicyMeta> policyMetas = null;
    private static boolean updated = false;

    @Autowired
    private PolicyMetaRepository policyMetaRepository;

    @Autowired
    ServiceParamRepository serviceParamsRepository;

    private static Date policyMetaModified = new Date();

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized Map<Long, PolicyMeta> getPolicyMetas() {

        Date policyMetaTableModified = policyMetaRepository.getAnyTableModified();

        if(policyMetas == null || dateBeforeDate(policyMetaModified, policyMetaTableModified)) {
            updateCache(policyMetaTableModified);
            updated = true;
        } else {
            updated = false;
        }

        ServiceParam serviceParam = serviceParamsRepository.findByCode(CACHED_TABLES_CHANED);
        if(serviceParam != null && serviceParam.isCachedTablesChanged()) {
            updateCache(policyMetaTableModified);
            serviceParam.setCachedTablesChanged(false);
            serviceParamsRepository.saveAndFlush(serviceParam);
            updated = true;
        }

        return policyMetas;
    }

    private void updateCache(Date policyMetaTableModified) {
        policyMetaModified = getLastModified(policyMetaTableModified);
        List<PolicyMeta> policyMetas = policyMetaService.getAllActivePolicyMeta();
        PolicyMetaCacheService.policyMetas = new HashMap<>();
        policyMetas.forEach(policyMeta -> {
            PolicyMetaCacheService.policyMetas.put(policyMeta.getId(), policyMetaService.getPolicyMetaById(policyMeta.getId()));
        });
        log.info("Cache updated!!!");
    }

    public PolicyMetaCacheService(PolicyMetaService policyMetaService) {
        this.policyMetaService = policyMetaService;
    }

    public boolean getUpdated() {
        return updated;
    }

    private boolean dateBeforeDate(Date date1, Date date2) {
        return date2 != null && date1.getTime() < date2.getTime();
    }

    private Date getLastModified(Date date) {
        return date != null ? date : new Date();
    }
}