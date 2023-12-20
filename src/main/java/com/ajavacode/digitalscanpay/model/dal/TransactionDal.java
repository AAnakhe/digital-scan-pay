package com.ajavacode.digitalscanpay.model.dal;

import com.ajavacode.digitalscanpay.model.Transaction;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface TransactionDal {
    Uni<List<Transaction>> getRecentTransactionsByPhoneNumber(String phoneNumber);

    Uni<Transaction> getMostRecentTransactionForQRCode(String phoneNumber);
}
