package com.ajavacode.digitalscanpay.controller;

import com.ajavacode.digitalscanpay.model.dal.TransactionDal;
import com.ajavacode.digitalscanpay.util.UrlBase;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.springframework.stereotype.Controller;

import java.util.function.Function;

@Controller
public class TransactionController implements Function<RoutingContext, Uni<Void>> {

    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final TransactionDal transactionDal;

    public TransactionController(Router router, TransactionDal transactionDal) {
        this.transactionDal = transactionDal;
        router.get(UrlBase.url("user/transactions/:phoneNumber"))
                .respond(this);
    }


    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        String phoneNumber = ctx.request().getParam("phoneNumber");

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return ctx.response().setStatusCode(400)
                    .putHeader(CONTENT, JSON_TYPE)
                    .end(new JsonObject().put("Oops!!", "phonenumber required")
                            .encode());
        }

        return transactionDal.getRecentTransactionsByPhoneNumber(phoneNumber)
                .flatMap(transactions -> {
                    if (transactions.isEmpty()) {
                        return ctx.response().setStatusCode(404)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("message", "No transactions found")
                                        .put("data", transactions)
                                        .encode());
                    }
                    return ctx.response()
                            .putHeader(CONTENT, JSON_TYPE)
                            .end(new JsonObject().put("message", "Transactions found")
                                    .put("data", transactions)
                                    .encode());
                });
    }
    }

