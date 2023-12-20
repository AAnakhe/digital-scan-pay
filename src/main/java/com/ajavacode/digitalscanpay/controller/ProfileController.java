package com.ajavacode.digitalscanpay.controller;

import com.ajavacode.digitalscanpay.model.dal.ProfileDal;
import com.ajavacode.digitalscanpay.util.UrlBase;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.springframework.stereotype.Controller;

import java.util.function.Function;

@Controller
public class ProfileController implements Function<RoutingContext, Uni<Void>> {

    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final ProfileDal profileDal;

    public ProfileController(Router router, ProfileDal profileDal) {
        this.profileDal = profileDal;
        router.get(UrlBase.url("user/profile/:phonenumber"))
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        String phoneNumber = ctx.request().getParam("phoneNumber");
        return profileDal.getUserProfile(phoneNumber)
                .flatMap(profile -> {
                    if (phoneNumber != null) {

                       return ctx.response()
                                .setStatusCode(200)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(profile.encode());
                    } else {
                        return ctx.response()
                                .setStatusCode(400)
                                .end(new JsonObject().put("message" ,"User profile not found").encode());
                    }
                });
    }
}
