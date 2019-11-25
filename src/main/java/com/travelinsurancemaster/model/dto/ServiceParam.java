package com.travelinsurancemaster.model.dto;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ServiceParam {

    static final long serialVersionUID = 1L;

    @Id
    @Column
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column
    private String code;

    @Column
    private boolean cachedTablesChanged;
}