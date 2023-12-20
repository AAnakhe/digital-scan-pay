package com.ajavacode.digitalscanpay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class JwtUser {
    private String uuid = UUID.randomUUID().toString();
    private String phoneNumber;
    private String password;
    private String nin;
    private String vehicleType;
    private String manufacturer;
    private String plateNumber;
    private String verification_token;
    private boolean isVerified;

    public JwtUser(String phoneNumber, String nin, String vehicleType, String string, String plateNumber, boolean b) {
    }
}
