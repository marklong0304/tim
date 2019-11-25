package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PolicyQuoteResponse {
    
    private Long policyId;
    private Long productGroupId;
    private Boolean hasPolicyChanged;
    private Boolean isAgentConfiguredToSellPolicy;
    private ValidationStatus status;
    private Double total;

    public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }

    public Long getProductGroupId() { return productGroupId; }
    public void setProductGroupId(Long productGroupId) { this.productGroupId = productGroupId; }

    public Boolean getHasPolicyChanged() { return hasPolicyChanged; }
    public void setHasPolicyChanged(Boolean hasPolicyChanged) { this.hasPolicyChanged = hasPolicyChanged; }

    public Boolean getIsAgentConfiguredToSellPolicy() { return isAgentConfiguredToSellPolicy; }
    public void setIsAgentConfiguredToSellPolicy(Boolean isAgentConfiguredToSellPolicy) { this.isAgentConfiguredToSellPolicy = isAgentConfiguredToSellPolicy; }

    public ValidationStatus getStatus() { return status; }
    public void setStatus(ValidationStatus status) { this.status = status; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}
