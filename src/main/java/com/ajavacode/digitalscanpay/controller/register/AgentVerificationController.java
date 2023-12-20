package com.ajavacode.digitalscanpay.controller.register;

import com.ajavacode.digitalscanpay.model.dal.OtpDal;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import org.springframework.stereotype.Controller;

import java.util.function.Function;

@Controller
public class AgentVerificationController implements Function<RoutingContext, Uni<Void>> {

    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final OtpDal otpDal;

    public AgentVerificationController(Router router, OtpDal otpDal) {
        this.otpDal = otpDal;
        router.post("/agent-signup-verification")
                .handler(BodyHandler.create())
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {

        String otp = ctx.body().asJsonObject().getString("otp");

        if (otp == null) {
            return ctx.response().setStatusCode(400)
                    .putHeader(CONTENT, JSON_TYPE)
                    .end(new JsonObject().put("error", "Invalid or missing 'otp'")
                            .encode());
        }
        return otpDal.verifyAgentOtp(otp)
                .flatMap(verified -> {
                    if (verified){
                        return ctx.response().setStatusCode(200)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("message", "OTP verified successfully")
                                        .encode());
                    }else {

                        return ctx.response().setStatusCode(400)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("error", "Invalid OTP")
                                        .encode());
                    }
                });
    }
}
