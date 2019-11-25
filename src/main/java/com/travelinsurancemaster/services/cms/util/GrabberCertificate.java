package com.travelinsurancemaster.services.cms.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.travelinsurancemaster.model.CountryCode;
import com.travelinsurancemaster.model.StateCode;
import com.travelinsurancemaster.model.dto.cms.certificate.Certificate;
import com.travelinsurancemaster.model.security.User;
import com.travelinsurancemaster.services.cms.CertificateService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Alexander.Isaenco
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrabberCertificate {
    public static final String DOWNLOAD_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SEPARATOR = ",";

    private String vendor_code;
    private String vendor_title;
    private String product_code;
    private String product_title;
    private String mode;
    private String download_file;
    private String download_time;
    private String countries;
    private String usa_states;
    private String canada_states;

    private boolean isDefault;

    public String getVendor_code() {
        return vendor_code;
    }

    public void setVendor_code(String vendor_code) {
        this.vendor_code = vendor_code;
    }

    public String getVendor_title() {
        return vendor_title;
    }

    public void setVendor_title(String vendor_title) {
        this.vendor_title = vendor_title;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDownload_file() {
        return download_file;
    }

    public void setDownload_file(String download_file) {
        this.download_file = download_file;
    }

    public String getDownload_time() {
        return download_time;
    }

    public void setDownload_time(String download_time) {
        this.download_time = download_time;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    public String getUsa_states() {
        return usa_states;
    }

    public void setUsa_states(String usa_states) {
        this.usa_states = usa_states;
    }

    public String getCanada_states() {
        return canada_states;
    }

    public void setCanada_states(String canada_states) {
        this.canada_states = canada_states;
    }

    @JsonIgnore
    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @JsonIgnore
    public Date getDownloadTime() {
        try {
            return new SimpleDateFormat(DOWNLOAD_TIME_FORMAT).parse(download_time);
        } catch (ParseException e) {
            return null;
        }
    }

    @JsonIgnore
    public Set<CountryCode> getCountryCodes() {
        return getValueList(countries, CountryCode.class);
    }

    @JsonIgnore
    public Set<StateCode> getUsaStateCodes() {
        return getValueList(usa_states, StateCode.class);
    }

    @JsonIgnore
    public Set<StateCode> getCanadaStateCodes() {
        return getValueList(canada_states, StateCode.class);
    }

    private <E extends Enum<E>> Set<E> getValueList(String value, final Class<E> enumClass) {
        if (StringUtils.isBlank(value)) {
            return new HashSet<>();
        }
        String[] split = value.split(SEPARATOR);
        if (ArrayUtils.isEmpty(split)) {
            return new HashSet<>();
        }
        Set<E> countryCodes = new HashSet<>(split.length);
        for (String c : split) {
            E anEnum = EnumUtils.getEnum(enumClass, c);
            if (anEnum != null) {
                countryCodes.add(anEnum);
            }
        }
        return countryCodes;
    }

    @JsonIgnore
    public String getCode() {
        return getVendor_code() + "_" + getProduct_code();
    }

    public Certificate toCertificate(User author) {
        Certificate certificate = new Certificate();
        certificate.setId(null);
        certificate.setAuthor(author);
        certificate.setVendorCode(vendor_code);
        certificate.setPolicyMetaCode(product_code);
        certificate.setCreateDate(getDownloadTime());
        certificate.setModifiedDate(getDownloadTime());
        certificate.setFileName(download_file);
        certificate.setMimeType(CertificateService.DEFAULT_MIME_TYPE);
        certificate.setUuid(UUID.randomUUID().toString());
        certificate.setDeleted(false);
        certificate.setDefaultPolicy(false);

        Set<CountryCode> countryCodes = getCountryCodes();
        Set<StateCode> states = new HashSet<>();
        Set<StateCode> usaStateCodes = getUsaStateCodes();
        if (CollectionUtils.isNotEmpty(usaStateCodes)) {
            states.addAll(usaStateCodes);
            countryCodes.add(CountryCode.US);
        }
        Set<StateCode> canadaStateCodes = getCanadaStateCodes();
        if (CollectionUtils.isNotEmpty(canadaStateCodes)) {
            states.addAll(canadaStateCodes);
            countryCodes.add(CountryCode.CA);
        }
        certificate.setCountries(countryCodes);
        certificate.setStates(states);

        return certificate;
    }
}
