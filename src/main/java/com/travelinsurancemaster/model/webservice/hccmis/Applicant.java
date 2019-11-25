package com.travelinsurancemaster.model.webservice.hccmis;

/**
 * Created by raman on 12.06.2019.
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.travelinsurancemaster.model.webservice.common.GenericTraveler;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Applicant {

    private String firstName;
    private String middleInitial;
    private String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/YYYY")
    private LocalDate dob;
    private String gender;
    private String citizenship;
    private String passport = "";

    public Applicant(GenericTraveler traveler, String gender, String citizenship) {
        this.firstName = traveler.getFirstName();
        this.middleInitial = traveler.getMiddleInitials();
        this.lastName = traveler.getLastName();
        this.dob = traveler.getBirthdaySafe();
        this.gender = gender;
        this.citizenship = citizenship;
    }
}