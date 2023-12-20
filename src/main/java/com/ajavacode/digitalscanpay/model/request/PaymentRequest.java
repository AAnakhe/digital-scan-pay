package com.ajavacode.digitalscanpay.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PaymentRequest {
    private String userUuid;
    private String phoneNumber;
    private String plateNumber;
    private String service;
    private String park;
    private BigDecimal amount;
    private String plan;
}
