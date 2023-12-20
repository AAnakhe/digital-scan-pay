package com.ajavacode.digitalscanpay.controller.register;

import com.ajavacode.digitalscanpay.contants.ContentTypeConstants;
import com.ajavacode.digitalscanpay.contants.LoggerConstants;
import com.ajavacode.digitalscanpay.model.dal.AgentDal;
import com.ajavacode.digitalscanpay.model.data.AgentSignUpData;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Controller;

import java.util.Set;
import java.util.function.Function;
@Controller
public class AgentSignUpController implements Function<RoutingContext, Uni<Void>> {

    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final AgentDal agentDal;

    public AgentSignUpController(Router router, AgentDal agentDal) {
        this.agentDal = agentDal;
        router.post("/agent-signup")
                .handler(BodyHandler.create())
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        AgentSignUpData signUp = ctx.body().asPojo(AgentSignUpData.class);

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Set<ConstraintViolation<AgentSignUpData>> violations = validatorFactory.getValidator().validate(signUp);

            if (!violations.isEmpty()) {
                return Uni.createFrom().completionStage(ctx.response()
                        .setStatusCode(400)
                        .putHeader(ContentTypeConstants.CONTENT, ContentTypeConstants.JSON_TYPE)
                        .end(new JsonObject().put("errors", convertViolationsToJson(violations)).encode())
                        .subscribeAsCompletionStage());
            }

            return agentDal.addAgent(ctx, signUp)
                    .map(user -> null);
        } catch (Exception e) {

            LoggerConstants.getLogger(AgentSignUpData.class).info("An error occurred during user signup processing)", e);
            return Uni.createFrom().failure(e);
        }
    }

    private JsonArray convertViolationsToJson(Set<ConstraintViolation<AgentSignUpData>> violations) {

        return violations.stream()
                .map(violation -> new JsonObject()
                        .put("property", violation.getPropertyPath().toString())
                        .put("message", violation.getMessage()))
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }
}
