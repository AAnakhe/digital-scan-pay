package com.ajavacode.digitalscanpay.controller;


import com.ajavacode.digitalscanpay.model.dal.JwtUserDal;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;


import java.util.function.Function;

@Controller
public class JwtSignupVerificationController implements Function<RoutingContext, Uni<Void>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtSignupVerificationController.class);

    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final JwtUserDal jwtUserDal;

    public JwtSignupVerificationController(Router router, JwtUserDal jwtUserDal) {
        this.jwtUserDal = jwtUserDal;
        router.get("/signupVerification/:token")
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        String token = ctx.request().getParam("token");

        return jwtUserDal.verifyUser(token)
                .flatMap(user -> {

                    if (user.isVerified()) {
                        LOGGER.info("User verification successful: {}", user);
                        return   ctx.response()
                                .setStatusCode(200)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("Verification Successful", user).encode());
                    } else {

                        LOGGER.warn("Token invalid or expired for token: {}", token);
                        return  ctx.response()
                                .setStatusCode(400)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("message", "Token invalid or expired").encode());
                    }
                });
    }

}

