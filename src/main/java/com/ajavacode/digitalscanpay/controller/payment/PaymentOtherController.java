package com.ajavacode.digitalscanpay.controller.payment;

import com.ajavacode.digitalscanpay.model.dal.PaymentDal;
import com.ajavacode.digitalscanpay.model.request.PaymentRequest;
import com.ajavacode.digitalscanpay.util.UrlBase;
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
public class PaymentOtherController implements Function<RoutingContext, Uni<Void>> {

    Logger LOGGER = LoggerFactory.getLogger(PaymentOtherController.class);
    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final PaymentDal paymentDal;

    public PaymentOtherController(Router router, PaymentDal paymentDal) {
        this.paymentDal = paymentDal;
        router.post(UrlBase.url("user/payment-other"))
                .handler(BodyHandler.create())
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {

        PaymentRequest paymentRequest = ctx.body().asPojo(PaymentRequest.class);


        return paymentDal.initiatePayment(paymentRequest)
                .flatMap(paymentSuccessful -> {
                    if (paymentSuccessful) {

                        return ctx.response().setStatusCode(200)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("message", "payment for user successful")
                                        .encode());
                    } else {
                        return ctx.response().setStatusCode(400)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("message", "No user found for phoneNumber: ")
                                        .encode());
                    }
                });
    }
}
