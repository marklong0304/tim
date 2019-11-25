package com.travelinsurancemaster.model.dto.json;

/**
 * Created by Chernov Artur on 19.09.2016.
 */
public class JsonCategoryValue {
    private String value;
    private boolean secondary;
    private String dataCategoryId;
    private String dataPolicyMetaId;
    private boolean showCertificate;

    public JsonCategoryValue(String dataCategoryId, String dataPolicyMetaId) {
        this.dataCategoryId = dataCategoryId;
        this.dataPolicyMetaId = dataPolicyMetaId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDataCategoryId() {
        return dataCategoryId;
    }

    public void setDataCategoryId(String dataCategoryId) {
        this.dataCategoryId = dataCategoryId;
    }

    public String getDataPolicyMetaId() {
        return dataPolicyMetaId;
    }

    public void setDataPolicyMetaId(String dataPolicyMetaId) {
        this.dataPolicyMetaId = dataPolicyMetaId;
    }

    public boolean isShowCertificate() {
        return showCertificate;
    }

    public void setShowCertificate(boolean showCertificate) {
        this.showCertificate = showCertificate;
    }

    public boolean isSecondary() {
        return secondary;
    }

    public void setSecondary(boolean secondary) {
        this.secondary = secondary;
    }
}
