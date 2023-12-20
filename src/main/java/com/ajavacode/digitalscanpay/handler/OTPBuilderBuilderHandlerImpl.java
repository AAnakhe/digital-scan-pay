package com.ajavacode.digitalscanpay.handler;

import com.ajavacode.digitalscanpay.service.OTPBuilderBuilderHandler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.auth.VertxContextPRNG;
import io.vertx.mutiny.ext.auth.jwt.JWTAuth;
import org.springframework.stereotype.Service;

@Service
public class OTPBuilderBuilderHandlerImpl implements OTPBuilderBuilderHandler {

    private final JWTAuth jwtAuth;
    private final Vertx vertx;

    public OTPBuilderBuilderHandlerImpl(JWTAuth jwtAuth, Vertx vertx) {
        this.jwtAuth = jwtAuth;
        this.vertx = vertx;
    }

    @Override
    public int generateOTP(int length) {

        VertxContextPRNG currentPRNG = VertxContextPRNG.current(vertx);
        return currentPRNG.nextInt(length);
    }

    @Override
    public String generateSignedOTP(int length) {

        int otp = generateOTP(length);

        JsonObject claims = new JsonObject().put("otp", otp);
        return jwtAuth.generateToken(claims, new JWTOptions()
                .setIssuer("aspacelife-tech")
                .setAlgorithm("RS256")
                .setExpiresInMinutes(2840));
    }

}
