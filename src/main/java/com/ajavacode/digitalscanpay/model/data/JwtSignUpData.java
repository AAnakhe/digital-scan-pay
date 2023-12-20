package com.ajavacode.digitalscanpay.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class JwtSignUpData {
    private String uuid = UUID.randomUUID().toString();
    private String phoneNumber;
    private String password;
    private String confirmPassword;
    private String nin;
    private String vehicleType;
    private String manufacturer;
    private String plateNumber;
}
