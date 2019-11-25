package com.travelinsurancemaster.model.webservice.common;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class Result {
    private Status status;
    private List<Error> errors = new ArrayList<>();

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return StringUtils.join(errors, ", ");
    }

    public void error(String code, String message) {
        setStatus(Status.ERROR);
        getErrors().add(new Error(code, message));
    }

    public static enum Status {
        SUCCESS, ERROR
    }

    public static class Error {
        private String errorCode;
        private String errorMsg;

        public Error() {
        }

        public Error(String errorCode, String errorMsg) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        @Override
        public String toString() {
            return errorMsg;
        }
    }

}
