package com.travelinsurancemaster.model.dto.json;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritchie on 2/17/16.
 */
public class JsonResponse {

    private boolean status;
    private List<FieldError> fieldErrorsList = new ArrayList<>();
    private List<String> globalErrors = new ArrayList<>();

    public JsonResponse() {
    }

    public JsonResponse(boolean status) {
        this.status = status;
    }

    public JsonResponse(boolean status, List<ObjectError> allErrors) {
        for (ObjectError objectError : allErrors) {
            if (objectError instanceof FieldError) {
                this.fieldErrorsList.add((FieldError) objectError);
            } else {
                this.globalErrors.add(objectError.getDefaultMessage());
            }
        }
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<FieldError> getFieldErrorsList() {
        return fieldErrorsList;
    }

    public void setFieldErrorsList(List<FieldError> fieldErrorsList) {
        this.fieldErrorsList = fieldErrorsList;
    }

    public List<String> getGlobalErrors() {
        return globalErrors;
    }

    public void setGlobalErrors(List<String> globalErrors) {
        this.globalErrors = globalErrors;
    }
}
