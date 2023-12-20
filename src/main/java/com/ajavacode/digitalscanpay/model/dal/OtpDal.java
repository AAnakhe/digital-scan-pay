package com.ajavacode.digitalscanpay.model.dal;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.ext.web.RoutingContext;

public interface OtpDal {
    Uni<Void> regenerateUserOtp(String phoneNumber, RoutingContext ctx);

    Uni<Boolean> verifyUserOtp(String otp);

    Uni<Boolean> verifyAgentOtp(String otp);

    Uni<Void> regenerateAgentOtp(String phoneNumber, RoutingContext ctx);
}
