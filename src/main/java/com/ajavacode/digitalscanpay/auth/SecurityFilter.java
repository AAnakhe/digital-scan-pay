package com.ajavacode.digitalscanpay.auth;

import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.http.Cookie;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.SessionHandler;
import io.vertx.mutiny.ext.web.sstore.SessionStore;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class SecurityFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);


    @Autowired
    public void handler(Router router, Vertx vertx) {
        RedisOptions redisOptions = new RedisOptions()
                .setConnectionString("redis://localhost:6379");
        Redis redisClient = Redis.createClient(vertx, redisOptions);

        redisClient.connect().subscribe().with(
                connection -> {
                    System.out.println("Connected to Redis");
                },
                error -> {
                    System.err.println("Failed to connect to Redis: " + error.getMessage());
                }
        );
        SessionStore sessionStore = SessionStore.create(vertx, JsonObject.mapFrom(redisClient));

        SessionHandler sessionHandler = SessionHandler.create(sessionStore)
                .setCookieHttpOnlyFlag(true)
                .setCookieSecureFlag(true)
                .setSessionTimeout(4000000);


        router.route("/login")
                .handler(sessionHandler);
        router.route("/agent-login")
                .handler(sessionHandler);

        router.route("/api/v1/user/*").handler(ctx -> {
            Cookie cookieSession = ctx.request().getCookie("vertx-web.session");
            Cookie roles = ctx.request().getCookie("roles");

            if (cookieSession != null && roles != null) {

                if (Objects.equals(cookieSession.getValue(), cookieSession.getValue())) {

                    LOGGER.info("Session ID: " + cookieSession.getValue());


                    if (roles.getValue().contains("USER") || roles.getValue().contains("AGENT")){
                        LOGGER.info("roles " + roles.getValue());
                        ctx.next();
                    }

                } else {
                    LOGGER.info("Unauthorized: Invalid session");
                    ctx.fail(401, new RuntimeException("Unauthorized"));
                }

            }else {
                ctx.fail(401, new Throwable("Unauthorised: Session not found"));
            }

        });
    }
}
