package com.ajavacode.digitalscanpay.controller;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.function.Function;

@Controller
public class SwaggerDocController implements Function<RoutingContext, Uni<Void>> {
    private static final String CONTENT = "content-type";
    private static final String YAML_TYPE = "application/yml";

    @Autowired
    public SwaggerDocController(Router router) {
        router.get("/api-doc")
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        return ctx.response().setStatusCode(200)
                .putHeader(CONTENT, YAML_TYPE)
                .sendFile("swagger.yml");
    }
}
