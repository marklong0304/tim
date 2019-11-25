package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"totalPremiumAmount", "standardPremium", "optionalPremium", "standardTax", "optionalTax",
        "extendedTax", "extendedPremium", "promotionalPremium", "feesPremium", "feesTax", "standardFeesPremium",
        "optionalFeesPremium", "tax", "standardPremiumDistribution", "packagePremiumDistribution"})
public class PremiumTG {
    @XmlElement(name = "TotalPremiumAmount")
    private BigDecimal totalPremiumAmount;
    @XmlElement(name = "StandardPremium")
    private int standardPremium;
    @XmlElement(name = "OptionalPremium")
    private int optionalPremium;
    @XmlElement(name = "StandardTax")
    private int standardTax;
    @XmlElement(name = "OptionalTax")
    private int optionalTax;
    @XmlElement(name = "ExtendedTax")
    private int extendedTax;
    @XmlElement(name = "ExtendedPremium")
    private int extendedPremium;
    @XmlElement(name = "PromotionalPremium")
    private int promotionalPremium;
    @XmlElement(name = "FeesPremium")
    private int feesPremium;
    @XmlElement(name = "FeesTax")
    private int feesTax;
    @XmlElement(name = "StandardFeesPremium")
    private int standardFeesPremium;
    @XmlElement(name = "OptionalFeesPremium")
    private int optionalFeesPremium;
    @XmlElement(name = "Tax")
    private int tax;
    @XmlElement(name = "StandardPremiumDistribution")
    private StandardPremiumDistributionTG standardPremiumDistribution;
    @XmlElement(name = "PackagePremiumDistribution")
    private PackagePremiumDistributionTG packagePremiumDistribution;

    public BigDecimal getTotalPremiumAmount() {
        return totalPremiumAmount;
    }

    public void setTotalPremiumAmount(BigDecimal totalPremiumAmount) {
        this.totalPremiumAmount = totalPremiumAmount;
    }

    public int getStandardPremium() {
        return standardPremium;
    }

    public void setStandardPremium(int standardPremium) {
        this.standardPremium = standardPremium;
    }

    public int getOptionalPremium() {
        return optionalPremium;
    }

    public void setOptionalPremium(int optionalPremium) {
        this.optionalPremium = optionalPremium;
    }

    public int getStandardTax() {
        return standardTax;
    }

    public void setStandardTax(int standardTax) {
        this.standardTax = standardTax;
    }

    public int getOptionalTax() {
        return optionalTax;
    }

    public void setOptionalTax(int optionalTax) {
        this.optionalTax = optionalTax;
    }

    public int getExtendedTax() {
        return extendedTax;
    }

    public void setExtendedTax(int extendedTax) {
        this.extendedTax = extendedTax;
    }

    public int getExtendedPremium() {
        return extendedPremium;
    }

    public void setExtendedPremium(int extendedPremium) {
        this.extendedPremium = extendedPremium;
    }

    public int getPromotionalPremium() {
        return promotionalPremium;
    }

    public void setPromotionalPremium(int promotionalPremium) {
        this.promotionalPremium = promotionalPremium;
    }

    public int getFeesPremium() {
        return feesPremium;
    }

    public void setFeesPremium(int feesPremium) {
        this.feesPremium = feesPremium;
    }

    public int getFeesTax() {
        return feesTax;
    }

    public void setFeesTax(int feesTax) {
        this.feesTax = feesTax;
    }

    public int getStandardFeesPremium() {
        return standardFeesPremium;
    }

    public void setStandardFeesPremium(int standardFeesPremium) {
        this.standardFeesPremium = standardFeesPremium;
    }

    public int getOptionalFeesPremium() {
        return optionalFeesPremium;
    }

    public void setOptionalFeesPremium(int optionalFeesPremium) {
        this.optionalFeesPremium = optionalFeesPremium;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public StandardPremiumDistributionTG getStandardPremiumDistribution() {
        return standardPremiumDistribution;
    }

    public void setStandardPremiumDistribution(StandardPremiumDistributionTG standardPremiumDistribution) {
        this.standardPremiumDistribution = standardPremiumDistribution;
    }

    public PackagePremiumDistributionTG getPackagePremiumDistribution() {
        return packagePremiumDistribution;
    }

    public void setPackagePremiumDistribution(PackagePremiumDistributionTG packagePremiumDistribution) {
        this.packagePremiumDistribution = packagePremiumDistribution;
    }
}

