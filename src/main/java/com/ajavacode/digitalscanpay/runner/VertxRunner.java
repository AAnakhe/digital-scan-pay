package com.ajavacode.digitalscanpay.runner;

import com.ajavacode.digitalscanpay.verticle.MainVerticle;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.mutiny.core.Vertx;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class VertxRunner implements CommandLineRunner {

    private final Logger LOGGER = Logger.getLogger(VertxRunner.class.getName());
    private final Vertx vertx;
    private final VerticleFactory verticleFactory;

    @Autowired
    public VertxRunner(Vertx vertx, VerticleFactory verticleFactory) {
        this.vertx = vertx;
        this.verticleFactory = verticleFactory;
    }

    @Override
    public void run(String... args) {
        LOGGER.log(Level.INFO, Arrays.toString(args));
        vertx.deployVerticle(String.format("%s:%s", verticleFactory.prefix(), MainVerticle.class.getName()))
                .onItem()
                .invoke(s -> LOGGER.log(Level.INFO, s))
                .onFailure()
                .invoke(t -> LOGGER.log(Level.SEVERE, t.getMessage()))
                .subscribeAsCompletionStage();
}

    @PreDestroy
    public void destroy(){
      vertx.close()
              .onFailure()
              .invoke(t -> LOGGER.log(Level.SEVERE, t.getMessage()))
              .subscribeAsCompletionStage();
}
}
