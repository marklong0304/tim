package com.travelinsurancemaster.model.dto.query;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Raman on 11.07.19.
 */

@Data
public class AffiliateUserBalance implements IAffiliateUserBalance {
    private Long affiliateUserId;
    private BigDecimal balance;
}