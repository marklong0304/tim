package com.travelinsurancemaster.model.webservice.travelguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ritchie on 3/5/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"success", "comments", "transactionId", "errors", "warnings", "packageWarnings", "debugInfo"})
public class StatusTG {
    @XmlElement(name = "Success")
    private String success;
    @XmlElement(name = "Comments")
    private CommentsTG comments;
    @XmlElement(name = "TransactionID")
    private String transactionId;
    @XmlElement(name = "Errors")
    private ErrorsTG errors;
    @XmlElement(name = "Warnings")
    private WarningsTG warnings;
    @XmlElement(name = "PackageWarnings")
    private PackageWarningsTG packageWarnings;
    @XmlElement(name = "DebugInfo")
    private DebugInfoTG debugInfo;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public CommentsTG getComments() {
        return comments;
    }

    public void setComments(CommentsTG comments) {
        this.comments = comments;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public ErrorsTG getErrors() {
        return errors;
    }

    public void setErrors(ErrorsTG errors) {
        this.errors = errors;
    }

    public WarningsTG getWarnings() {
        return warnings;
    }

    public void setWarnings(WarningsTG warnings) {
        this.warnings = warnings;
    }

    public PackageWarningsTG getPackageWarnings() {
        return packageWarnings;
    }

    public void setPackageWarnings(PackageWarningsTG packageWarnings) {
        this.packageWarnings = packageWarnings;
    }

    public DebugInfoTG getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(DebugInfoTG debugInfo) {
        this.debugInfo = debugInfo;
    }
}
