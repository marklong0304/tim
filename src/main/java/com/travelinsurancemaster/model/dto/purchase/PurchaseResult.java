package com.travelinsurancemaster.model.dto.purchase;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by raman on 15.08.19.
 */

@Data
@Entity
public class PurchaseResult {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @Column
    private String page;

}