package com.smartfarming.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String username;
    private String phone;
    private String email;
    private String farmProvince;
    private String farmCity;
    private String farmDistrict;
    private String farmAddress;
    private String homeAddress;
    private String personalInfo;
}
