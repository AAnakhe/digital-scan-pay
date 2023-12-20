package com.ajavacode.digitalscanpay.controller;

import com.ajavacode.digitalscanpay.model.dal.UserDal;
import com.ajavacode.digitalscanpay.util.UrlBase;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.springframework.stereotype.Controller;

import java.util.function.Function;

@Controller
public class QrCodeController2 implements Function<RoutingContext, Uni<Void>> {

    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final UserDal userDal;

    public QrCodeController2(Router router, UserDal userDal) {
        this.userDal = userDal;
        router.get(UrlBase.url("user/generate-qr-code2/:phoneNumber"))
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        String phoneNumber = ctx.request().getParam("phoneNumber");

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return ctx.response()
                    .setStatusCode(400)
                    .putHeader(CONTENT, JSON_TYPE)
                    .end(new JsonObject().put("message", "Invalid or missing phoneNumber").encode());
        } else {
            return userDal.getUserAndUniqueID(phoneNumber)
                    .flatMap(qrCode -> {
                        if (qrCode == null) {
                            return ctx.response()
                                    .setStatusCode(404)
                                    .putHeader(CONTENT, JSON_TYPE)
                                    .end("{}");
                        }

//                        JsonObject json = new JsonObject();
//                        json.put("name", qrCode.getUser().getName());
//                        json.put("plateNumber", qrCode.getUser().getPlateNumber());
//                        json.put("uniqueID", qrCode.getUser().getUniqueID());

                        return ctx.response()
                                .setStatusCode(200)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(qrCode.encode());

                    });
        }
    }
}
