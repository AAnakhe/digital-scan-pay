package com.ajavacode.digitalscanpay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class   Payment {
    private UUID paymentId;
    private UUID userUuid;
    private String plateNumber;
    private String service;
    private String park;
    private BigDecimal amount;
    private String plan;
    private LocalDateTime paymentTime;
    private String status;
}
