package com.ajavacode.digitalscanpay.model.dal;

import com.ajavacode.digitalscanpay.model.request.PaymentRequest;
import io.smallrye.mutiny.Uni;

public interface PaymentDal {

    Uni<Boolean> initiatePayment(PaymentRequest parentRequest);
}
