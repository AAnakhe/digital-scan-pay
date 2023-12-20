package com.ajavacode.digitalscanpay.verticle;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.mutiny.ext.web.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());

    private final Router router;
    private final Integer serverPort;

    @Autowired
    public MainVerticle(Router router, @Value("${vertx.spring.port}") Integer serverPort) {
        this.router = router;
        this.serverPort = serverPort;
    }

    @Override
    public Uni<Void> asyncStart() {
        return vertx.createHttpServer().requestHandler(router)
                .listen(serverPort)
                .onItem()
                .invoke(s -> LOGGER.log(Level.INFO, "Server Running on http://localhost" + s.actualPort()))
                .onFailure()
                .invoke(t -> LOGGER.log(Level.SEVERE, t.getMessage()))
                .replaceWithVoid();
    }
 }
