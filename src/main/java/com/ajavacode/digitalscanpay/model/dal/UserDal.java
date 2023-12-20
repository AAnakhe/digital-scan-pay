package com.ajavacode.digitalscanpay.model.dal;

import com.ajavacode.digitalscanpay.model.User;
import com.ajavacode.digitalscanpay.model.data.LoginData;
import com.ajavacode.digitalscanpay.model.data.UserSignUpData;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.FileUpload;
import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.List;

public interface UserDal {
    Uni<User> getUser(String phoneNumber);
    Uni<JsonObject> getUserAndUniqueID(String phoneNumber);

    Uni<Void> add(RoutingContext ctx, UserSignUpData signUpData);

    Uni<Void> updateUserDriversLicensePath(String phoneNumber, String imageFilePath);

    Uni<Boolean> isValidCredentials(LoginData loginData);

    Uni<byte[]> readUploadedFile(FileUpload uploadedFile, Vertx vertx);

    String generateUniqueFileName(String originalFileName);

    Uni<List<String>> getUserRoles(String phoneNumber);
}
