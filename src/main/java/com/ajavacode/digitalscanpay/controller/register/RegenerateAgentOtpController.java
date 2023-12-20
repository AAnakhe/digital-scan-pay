package com.ajavacode.digitalscanpay.controller.register;

import com.ajavacode.digitalscanpay.model.dal.OtpDal;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.springframework.stereotype.Controller;

import java.util.function.Function;

@Controller
public class RegenerateAgentOtpController implements Function<RoutingContext, Uni<Void>> {

    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final OtpDal otpDal;

    public RegenerateAgentOtpController(Router router, OtpDal otpDal) {
        this.otpDal = otpDal;
        router.get("/regenerate-agent-otp/:phoneNumber")
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        String phoneNumber = ctx.request().getParam("phoneNumber");

        if (phoneNumber == null) {
            return ctx.response()
                    .setStatusCode(500)
                    .putHeader(CONTENT, JSON_TYPE)
                    .end(new JsonObject().put("error", "Failed to regenerate OTP").encode());
        }
        return otpDal.regenerateAgentOtp(phoneNumber, ctx);
    }
}
