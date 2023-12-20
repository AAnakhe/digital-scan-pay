package com.ajavacode.digitalscanpay.controller.auth;

import com.ajavacode.digitalscanpay.model.dal.AgentDal;
import com.ajavacode.digitalscanpay.model.data.LoginData;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.http.Cookie;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.Session;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.function.Function;

@Controller
public class AgentLoginController implements Function<RoutingContext, Uni<Void>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginController.class);
    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";

    private final AgentDal agentDal;



    public AgentLoginController(Router router, AgentDal agentDal) {
        this.agentDal = agentDal;

        router.post("/agent-login")
                .handler(BodyHandler.create())
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        LoginData credentials = ctx.body().asPojo(LoginData.class);

        return agentDal.isValidCredentials(credentials)
                .flatMap(login -> {
                    if (login) {
                        return agentDal.getAgentRoles(credentials.getPhoneNumber())
                                .flatMap(roles -> {
                                    Session session = ctx.session()
                                            .put("agentId", credentials.getPhoneNumber())
                                            .put("role", roles);
                                    session.timeout();
                                    String sessionCookie = session.id();

                                    ctx.response().addCookie(Cookie.cookie("vertx-web.session", sessionCookie)
                                            .setSecure(true)
                                            .setHttpOnly(true));
                                    ctx.response().addCookie(Cookie.cookie("roles", roles.toString())
                                            .setSecure(true)
                                            .setHttpOnly(true));


                                    LOGGER.info("Agent logged in successfully. AgentPhoneNumber: {}", credentials.getPhoneNumber());
                                    return ctx.response().setStatusCode(200)
                                            .putHeader(CONTENT, JSON_TYPE)
                                            .end(new JsonObject().put("message", "Login Successful")
                                                    .put("vertx-web.session", session.id())
                                                    .put("roles", session.get("role"))
                                                    .encode());
                                });
                    } else {
                        LOGGER.info("Login failed for phoneNumber: {}", credentials.getPhoneNumber());
                        return ctx.response().setStatusCode(401)
                                .putHeader(CONTENT, JSON_TYPE)
                                .end(new JsonObject().put("Unauthorised!", "Incorrect Phonenumber or password")
                                        .encode());
                    }
                });
    }
}
