package com.travelinsurancemaster.services.system;

import com.travelinsurancemaster.model.PercentType;
import com.travelinsurancemaster.model.dto.system.SystemSettings;
import com.travelinsurancemaster.repository.system.SystemSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Created by Chernov Artur on 25.08.15.
 */

@Service
@Transactional
public class SystemSettingsService {
    private final static Logger log = LoggerFactory.getLogger(SystemSettingsService.class);

    private static final String DEFAULT = "DEFAULT";

    @Autowired
    private SystemSettingsRepository systemSettingsRepository;

    public SystemSettings getSystemSettings(String name) {
        return systemSettingsRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public SystemSettings getDefault() {
        return systemSettingsRepository.findByName(DEFAULT);
    }

    public SystemSettings updateDefaultCommission(SystemSettings settings) {
        log.debug("Update systemSettings={}", settings);
        SystemSettings existedSystemSettings = getDefault();
        existedSystemSettings.setDefaultLinkPercentType(settings.getDefaultLinkPercentType());
        existedSystemSettings.getDefaultLinkPercentInfo().clear();
        if (settings.getDefaultLinkPercentType().getId() != PercentType.NONE.getId()) {
            existedSystemSettings.getDefaultLinkPercentInfo().addAll(settings.getDefaultLinkPercentInfo());
        }
        return systemSettingsRepository.save(existedSystemSettings);
    }

    public SystemSettings updatePlanDescriptionCategories(SystemSettings settings) {
        log.debug("Update systemSettings={}", settings);
        SystemSettings existedSystemSettings = getDefault();
        existedSystemSettings.setPlanDescriptionCategory1(settings.getPlanDescriptionCategory1());
        existedSystemSettings.setPlanDescriptionCategory2(settings.getPlanDescriptionCategory2());
        existedSystemSettings.setPlanDescriptionCategory3(settings.getPlanDescriptionCategory3());
        existedSystemSettings.setPlanDescriptionCategory4(settings.getPlanDescriptionCategory4());
        existedSystemSettings.setPlanDescriptionCategory5(settings.getPlanDescriptionCategory5());
        existedSystemSettings.setPlanDescriptionCategory6(settings.getPlanDescriptionCategory6());
        return systemSettingsRepository.save(existedSystemSettings);
    }

    public SystemSettings updateDefaultPhone(SystemSettings settings) {
        SystemSettings existedSystemSettings = getDefault();
        existedSystemSettings.setPhone(settings.getPhone());
        return systemSettingsRepository.save(existedSystemSettings);
    }

    public SystemSettings getSystemSettingsByCategoryId(String categoryId) {
        return systemSettingsRepository.findByCategoryId(categoryId);
    }
}
