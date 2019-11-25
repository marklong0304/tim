package com.travelinsurancemaster.model.dto.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Artur Chernov
 */
public class FailureAjaxResponse<T> implements AjaxResponse, Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> errors = new ArrayList<>();

    public FailureAjaxResponse(List<T> errors) {
        this.errors = errors;
    }

    public List<T> getErrors() {
        return errors;
    }

    public void setErrors(List<T> errors) {
        this.errors = errors;
    }
}
