package com.ajavacode.digitalscanpay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Agent {
    private String uuid = UUID.randomUUID().toString();
    private String name;
    private String phoneNumber;
    private String password;
    private String gender;
    private String nin;
    private String driversLicense;
    private List<String> roles;
    private boolean isVerified;
    private String secretKey;
    private String otp;
}
