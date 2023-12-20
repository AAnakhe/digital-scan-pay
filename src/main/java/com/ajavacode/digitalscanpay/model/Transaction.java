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
public class Transaction {
    private UUID transactionId;
    private UUID userUuid;
    private String serviceType;
    private BigDecimal amount;
    private LocalDateTime transactionTime;
}
