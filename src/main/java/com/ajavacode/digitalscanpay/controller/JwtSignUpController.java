package com.ajavacode.digitalscanpay.controller;

import com.ajavacode.digitalscanpay.model.JwtUser;
import com.ajavacode.digitalscanpay.model.dal.JwtUserDal;
import com.ajavacode.digitalscanpay.model.data.JwtSignUpData;
import com.ajavacode.digitalscanpay.service.JwtBuilderHandler;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Objects;
import java.util.function.Function;

@Controller
public class JwtSignUpController implements Function<RoutingContext, Uni<Void>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtSignUpController.class);

    private final JwtBuilderHandler jwtBuilderHandler;
    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final JwtUserDal jwtUserDal;

    public JwtSignUpController(Router router, JwtBuilderHandler jwtBuilderHandler, JwtUserDal jwtUserDal) {
        this.jwtBuilderHandler = jwtBuilderHandler;
        this.jwtUserDal = jwtUserDal;

        router.post("/signup")
                .handler(BodyHandler.create())
                .respond(this);
    }


    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        JwtSignUpData signup = ctx.body().asPojo(JwtSignUpData.class);
        return jwtUserDal.add(signup)
                .flatMap(user -> {
                    if (!Objects.equals(signup.getPassword(), signup.getConfirmPassword())) {
                        return ctx.response().setStatusCode(400)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("message", "Password does not match")
                                        .encode());
                    } else {
                        return ctx.response()
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("jwt", jwtBuilderHandler.buildToken(new JwtUser()))
                                        .encode()).onFailure().invoke(Throwable::printStackTrace);
                    }
                });
    }
}
