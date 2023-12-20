package com.ajavacode.digitalscanpay.controller;

import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.handler.StaticHandler;
import org.springframework.stereotype.Controller;

@Controller
public class SwaggerUIController {
    public SwaggerUIController(Router router) {
        router.route("/doc/*").handler(StaticHandler.create("webroot/swagger"));
    }
}
