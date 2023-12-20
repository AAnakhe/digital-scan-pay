package com.ajavacode.digitalscanpay.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginData {
    private String phoneNumber;
    private String password;
}
