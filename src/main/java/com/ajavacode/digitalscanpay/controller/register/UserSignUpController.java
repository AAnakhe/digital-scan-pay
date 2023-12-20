package com.ajavacode.digitalscanpay.controller.register;


import com.ajavacode.digitalscanpay.contants.ContentTypeConstants;
import com.ajavacode.digitalscanpay.contants.LoggerConstants;
import com.ajavacode.digitalscanpay.model.dal.UserDal;
import com.ajavacode.digitalscanpay.model.data.UserSignUpData;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Set;
import java.util.function.Function;

@Controller
public class UserSignUpController implements Function<RoutingContext, Uni<Void>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSignUpController.class);


    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final UserDal userDal;

    public UserSignUpController(Router router, UserDal userDal) {
        this.userDal = userDal;
        router.post("/user-signup").handler(BodyHandler.create())
                .respond(this);
    }


    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        UserSignUpData signUp = ctx.body().asPojo(UserSignUpData.class);

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Set<ConstraintViolation<UserSignUpData>> violations = validatorFactory.getValidator().validate(signUp);

            if (!violations.isEmpty()) {
                return Uni.createFrom().completionStage(ctx.response()
                        .setStatusCode(400)
                        .putHeader(ContentTypeConstants.CONTENT, ContentTypeConstants.JSON_TYPE)
                        .end(new JsonObject().put("errors", convertViolationsToJson(violations)).encode())
                        .subscribeAsCompletionStage());
            }

            return userDal.add(ctx, signUp)
                    .map(user -> null);
        } catch (Exception e) {

            LoggerConstants.getLogger(UserSignUpData.class).info("An error occurred during user signup processing)", e);
            return Uni.createFrom().failure(e);
        }
    }

    private  JsonArray convertViolationsToJson(Set<ConstraintViolation<UserSignUpData>> violations) {

        return violations.stream()
                .map(violation -> new JsonObject()
                        .put("property", violation.getPropertyPath().toString())
                        .put("message", violation.getMessage()))
                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }
}
