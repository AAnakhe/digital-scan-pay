package com.ajavacode.digitalscanpay.model.dal;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;

public interface ProfileDal {

    Uni<JsonObject> getUserProfile(String phoneNumber);
}
