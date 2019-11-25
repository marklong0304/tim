package com.travelinsurancemaster.model.webservice.common.forms;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by Chernov Artur on 26.04.15.
 */
public class Step3QuoteRequestForm implements Serializable {

    static final long serialVersionUID = 1L;

    private boolean preExistingMedicalAndCancellation;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate depositDate;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate paymentDate;

    public boolean isPreExistingMedicalAndCancellation() {
        return preExistingMedicalAndCancellation;
    }

    public void setPreExistingMedicalAndCancellation(boolean preExistingMedicalAndCancellation) {
        this.preExistingMedicalAndCancellation = preExistingMedicalAndCancellation;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(LocalDate depositDate) {
        this.depositDate = depositDate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}