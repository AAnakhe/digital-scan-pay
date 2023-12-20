package com.ajavacode.digitalscanpay.config;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class Configurations {

    public Configurations() {
        Logger LOGGER = Logger.getLogger(Configurations.class.getName());
        LOGGER.log(Level.INFO, "Starting...");
    }

    @Bean
    public Vertx vertx(VerticleFactory verticleFactory){
        Vertx vertx = Vertx.vertx();
        vertx.registerVerticleFactory(verticleFactory);
        return vertx;
    }

    @Bean
    @Autowired
    public Router router(Vertx vertx){
        return Router.router(vertx);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public void authenticate(JsonObject jsonObject, Handler<AsyncResult<User>> handler) {

            }
        };
    }
}
