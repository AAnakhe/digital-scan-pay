package com.ajavacode.digitalscanpay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    private String uuid = UUID.randomUUID().toString();
    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private String uniqueID;
    private String gender;
    private String nin;
    private String vehicleType;
    private String manufacturer;
    private String plateNumber;
    private String driversLicense;
    private List<String> roles;
    private boolean isVerified;
    private String secretKey;
    private String otp;

    public User(boolean b) {
    }

    public User(boolean b, String verificationFailed) {
    }
}
