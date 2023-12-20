package com.ajavacode.digitalscanpay.controller;

import com.ajavacode.digitalscanpay.model.JwtUser;
import com.ajavacode.digitalscanpay.service.JwtBuilderHandler;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.springframework.stereotype.Controller;

import java.util.function.Function;

@Controller
public class JwtAuthController implements Function<RoutingContext, Uni<Void>> {

    JwtBuilderHandler jwtBuilderHandler;

    public JwtAuthController(Router router, JwtBuilderHandler jwtBuilderHandler) {
        this.jwtBuilderHandler = jwtBuilderHandler;
        router.get("/api/v1/user/getJwt").respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        return ctx.response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json")
                .end(new JsonObject().put("jwt", jwtBuilderHandler.buildToken(new JwtUser()))
                        .encode()).onFailure().invoke(Throwable::printStackTrace);
    }
}
