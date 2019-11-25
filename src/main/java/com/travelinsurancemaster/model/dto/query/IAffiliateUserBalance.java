package com.travelinsurancemaster.model.dto.query;

import java.math.BigDecimal;

/**
 * Created by Raman on 11.07.19.
 */

public interface IAffiliateUserBalance {
    Long getAffiliateUserId();
    BigDecimal getBalance();
}