package com.travelinsurancemaster.model.webservice.travelexinsurance;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Raman on 17.09.2019.
 */

@Data
@AllArgsConstructor
public class GenerateKey {
    private String mid;
    private String userID;
    private String password;
    private String developerID;
}
