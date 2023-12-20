package com.ajavacode.digitalscanpay.service;

public interface OtpUtil {
    String generateSecretKey();
    String generateOtp(String secretKey);

    String generateUniqueID();
}
