package com.ajavacode.digitalscanpay.config;

import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.auth.jwt.JWTAuth;
import io.vertx.mutiny.ext.web.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAuthConfigurations {

//    @Value("${aspacelife.smallrye.jwt.new-token.signature-algorithm}")
//    private String algorithm;

    @Bean
    @Autowired
    public JWTAuth jwtAuth(Vertx vertx, Router router) {
        JWTAuthOptions options = new JWTAuthOptions()
                // add public key
                .addPubSecKey(new PubSecKeyOptions()
                        .setAlgorithm("RS256")
                        .setBuffer("-----BEGIN PUBLIC KEY-----\n" +
                                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt3e+4qQIc5eILxBflI8x\n" +
                                "CfL43yFhoUKyNgp8HHytgmu8q93X7HL6WXYTXBAPVQEjcMcoIcoVbneTnnMJOa7Q\n" +
                                "D9nCL5j/2SBbrvrs4S3pNLKiaNBWJTyHZxAzcsjs9k6xlmMMCfsj15A5xE2fh4d4\n" +
                                "GEFtvUCfqiRwmkG00VzRf++3SwNNHsbhZpAqIgjTdiFeSoyn8zLx2qXHOlI+LTyO\n" +
                                "c7yRG1PX+xdpzYT9U0E0p+DHsuNWwfNKo8ylGvaIvOzP9MWQT9Wl4bqSiU3FNDQA\n" +
                                "f7zYRvcJuquc/+6z+d992q/STqOVRZSZY2vK2FFaf4l9bbCzl8bs3R5+fDZhwEqR\n" +
                                "yQIDAQAB\n" +
                                "-----END PUBLIC KEY-----"))
                // add private key
                .addPubSecKey(new PubSecKeyOptions()
                        .setAlgorithm("RS256")
                        .setBuffer("-----BEGIN PRIVATE KEY-----\n" +
                                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC3d77ipAhzl4gv\n" +
                                "EF+UjzEJ8vjfIWGhQrI2CnwcfK2Ca7yr3dfscvpZdhNcEA9VASNwxyghyhVud5Oe\n" +
                                "cwk5rtAP2cIvmP/ZIFuu+uzhLek0sqJo0FYlPIdnEDNyyOz2TrGWYwwJ+yPXkDnE\n" +
                                "TZ+Hh3gYQW29QJ+qJHCaQbTRXNF/77dLA00exuFmkCoiCNN2IV5KjKfzMvHapcc6\n" +
                                "Uj4tPI5zvJEbU9f7F2nNhP1TQTSn4Mey41bB80qjzKUa9oi87M/0xZBP1aXhupKJ\n" +
                                "TcU0NAB/vNhG9wm6q5z/7rP5333ar9JOo5VFlJlja8rYUVp/iX1tsLOXxuzdHn58\n" +
                                "NmHASpHJAgMBAAECggEAG7i5vIgZiNHAL5BTSOC8AatXZGuoU1P/FoBOEYXbN8Ys\n" +
                                "q3FpU15qKoNXWIVKs8bown0tOLHOvDLQONRb8/3QjVyTZVrCad3kTJO4oCuuPZqy\n" +
                                "RJEaOyUitnVxuKnYeD0aw0TzCD0MxOETgXBTJwjWoh+cw8hUd8JqyzwbZLkYJLLj\n" +
                                "w1ArsfPFRSwAFOCo4H8Lz+Tw3Bl6YEZqE6smlW03Vu8sQD7+PApWCAE96qst7R/w\n" +
                                "t7s5el7h1r5aGOJISgcAUqkJ/pfAQX5WUrGWeh05kgP9NDWgVwudak83uHI/S1A5\n" +
                                "DWGprAia0ycQtgoVNs51ElaxiHw0xa8453oMP1XASQKBgQDoDJAr8WBDBUqcCTI2\n" +
                                "qsoYOxOwRYpFHF8pjDtlDSYb63GE5r7kvcvHrFpVAvpN/nZdVM4GaHoLVLXVdrfB\n" +
                                "UVYhG85Sme9dV7rY8TjmCt6r4/ESIioc/PrOmZeFni2h/xqrKy8AgkpK1j4C517J\n" +
                                "02VitZWw95WtheUZ1Ot4epmY1QKBgQDKZ4R2k5GutP/gkXiBIOnIQMDAiO1j4rhA\n" +
                                "oucAnMOwJZpEOplbwikHSvMNPgktQYG89dy34nECDJsJGsev37wBGsaYrjcTL+BR\n" +
                                "yMoDPubSNF+QhX3XkE77qy5fcm62/buV34K4his5MOca4oO0d6t7WLReM7lQJbsG\n" +
                                "qcaRmRcPJQKBgBEWa79wpOAKkbXvz2zLItqp7wc597ajIjbTBsSoNTg1HETnIQOD\n" +
                                "HnBztqsv7vKzXE//ILTEGMig0ksH1Pw9WxRREd5vS3saLL4w9Tmrbz5FKMlnDqbX\n" +
                                "0jhIgynpvf6B5JraMTneXk4ofKSGGSrPkFW2fXFsqe7/PyGp1jUnAgyJAoGBAJL5\n" +
                                "SlnkXGQ1sJx8Zbm2kaxFQPJOO7tNdagR4pYyP3MYSwYCWzwjKDHQfd9+zaEcMT5q\n" +
                                "dgTdHFx1lNqupJQUbeuLauyRjRKUsFoS6IYk1e2L9Fz4YmH2uzPKoNjUOosMgnuZ\n" +
                                "Gs4tNxPHa/m8xBB22YGu7wJ8orMNQtray4MMXI81AoGACUabAQLvQXA819KssZ8c\n" +
                                "C98xNOGy137v23AYA7N+4y5VCdri4a76UVL2Wb1A++9mY49jogVQC8fF4gKed12q\n" +
                                "Qjg0TlMSMMw8sGaMBgjDbcuPSNAshXb9wG+2fIVPDtMs4Jg0Fc6+iG3+psSCiVY3\n" +
                                "I/SLh7ewBJ/TiVv7bCtZnDM=\n" +
                                "-----END PRIVATE KEY-----"));

        JWTAuth jwt = JWTAuth.create(vertx, options);


//        router.route("/api/*").handler(JWTAuthHandler.create(jwt));
        return jwt;
    }


}
