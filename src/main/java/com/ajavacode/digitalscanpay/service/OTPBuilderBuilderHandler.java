package com.ajavacode.digitalscanpay.service;

public interface OTPBuilderBuilderHandler {
    int generateOTP(int length);

    String generateSignedOTP(int length);
}
