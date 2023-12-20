package com.ajavacode.digitalscanpay.controller.register;

import com.ajavacode.digitalscanpay.model.dal.OtpDal;
import io.smallrye.mutiny.Uni;

import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.function.Function;

@Controller
public class UserVerificationController implements Function<RoutingContext, Uni<Void>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserVerificationController .class);
    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";

    private final OtpDal otpDal;

    public UserVerificationController(Router router, OtpDal otpDal) {
        this.otpDal = otpDal;
        router.post("/user-signup-verification")
                .handler(BodyHandler.create())
                .respond(this);
    }


    @Override
    public Uni<Void> apply(RoutingContext ctx) {

        String otp = ctx.body().asJsonObject().getString("otp");
        return otpDal.verifyUserOtp(String.valueOf(otp))
                .flatMap(validOTP -> {
                    if (!validOTP) {
                        return ctx.response()
                                .setStatusCode(400)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("Otp", "Verification failed")
                                        .encode());
                    } else {
                        LOGGER.info("User otp verification successful");
                        return ctx.response()
                                .setStatusCode(200)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("Otp", "Verification successful")
                                        .encode());
                    }
                });

    }
}
