package com.travelinsurancemaster.model.webservice.sevencorners;

/**
 * Created by raman on 17.04.2019.
 */

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersonField {

    private String personIdentifier;
    private List<Field> fields;

    public String getPersonIdentifier() { return personIdentifier; }
    public void setPersonIdentifier(String personIdentifier) { this.personIdentifier = personIdentifier; }
    
    public List<Field> getFields() { return fields; }
    public void setFields(List<Field> fields) { this.fields = fields; }
}
