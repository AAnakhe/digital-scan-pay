package com.ajavacode.digitalscanpay.controller;

import com.ajavacode.digitalscanpay.service.QrCodeService;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.function.Function;

@Controller
public class QrCodeController implements Function<RoutingContext, Uni<Void>> {

    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";

    private final QrCodeService qrCodeService;

    @Autowired
    public QrCodeController(Router router, QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
        router.get("/generate-qr-code/:phoneNumber")
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        String phoneNumber = ctx.request().getParam("phoneNumber");

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return ctx.response()
                    .setStatusCode(400)
                    .end("Invalid or missing phoneNumber");
        }else {
            return qrCodeService.generateQRCodeForUserAndTransaction(phoneNumber)
                    .flatMap(qrCode -> ctx.response()
                            .setStatusCode(200)
                            .putHeader(CONTENT, JSON_TYPE)
                            .end(Buffer.buffer(qrCode)));
        }

    }
}
