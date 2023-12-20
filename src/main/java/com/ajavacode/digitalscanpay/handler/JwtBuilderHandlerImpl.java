package com.ajavacode.digitalscanpay.handler;

import com.ajavacode.digitalscanpay.model.JwtUser;
import com.ajavacode.digitalscanpay.service.JwtBuilderHandler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.mutiny.ext.auth.jwt.JWTAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class JwtBuilderHandlerImpl implements JwtBuilderHandler {

    private final JWTAuth jwtAuth;

//    @Value("${aspacelife.smallrye.jwt.new-token.signature-algorithm}")
//    private String algorithm;
//    @Value("${aspacelife.smallrye.jwt.sign.key.location}")
//    private String location;
//    @Value("${aspacelife.smallrye.jwt.new-token.issuer}")
//    private String issuer;


    @Autowired
    public JwtBuilderHandlerImpl(JWTAuth jwtAuth) {
        this.jwtAuth = jwtAuth;
    }

    @Override
    public String buildToken(JwtUser jwtUser) {
        JsonObject claims = new JsonObject()
                .put("sub", "aspacelife-tech");
//                .put("role", user.getRole());

        return jwtAuth.generateToken(claims, new JWTOptions()
                .setIssuer("aspacelife-tech")
                .setAlgorithm("RS256")
                .setExpiresInMinutes(2840));
    }
}
