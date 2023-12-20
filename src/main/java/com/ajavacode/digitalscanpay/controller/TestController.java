package com.ajavacode.digitalscanpay.controller;

import com.ajavacode.digitalscanpay.model.dal.UserDal;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import org.springframework.stereotype.Controller;

import java.util.function.Function;

@Controller
public class TestController implements Function<RoutingContext, Uni<Void>> {

    private final UserDal userDal;

    public TestController(Router router, UserDal userDal) {
        this.userDal = userDal;
        router.get("/get-user/:phoneNumber")
                .handler(BodyHandler.create())
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        String phoneNumber = ctx.request().getParam("phoneNumber");

        if (phoneNumber != null) {
            return userDal.getUser(phoneNumber)
                            .flatMap(result -> ctx.response()
                                    .setStatusCode(200)
                                    .putHeader("content-type", "application/json")
                                    .end(new JsonObject().put("message", result).encode()));
        }

        return null;
    }
}
