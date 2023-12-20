package com.ajavacode.digitalscanpay.controller;

import com.ajavacode.digitalscanpay.model.dal.UserDal;

import com.ajavacode.digitalscanpay.config.CloudinaryStorageConfig;
import com.ajavacode.digitalscanpay.util.UrlBase;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.FileUpload;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.function.Function;

@Controller
public class UploadLicenseController implements Function<RoutingContext, Uni<Void>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadLicenseController.class);
    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final CloudinaryStorageConfig cloudinaryStorageConfig;
    private final UserDal userDal;
    private final Vertx vertx;

    public UploadLicenseController(Router router, CloudinaryStorageConfig cloudinaryStorageConfig, UserDal userDal, Vertx vertx) {
        this.cloudinaryStorageConfig = cloudinaryStorageConfig;
        this.userDal = userDal;
        this.vertx = vertx;
        router.post(UrlBase.url("user/upload-drivers-license/:phoneNumber"))
                .handler(BodyHandler.create())
                .respond(this);
    }

    @Override
    public Uni<Void> apply(RoutingContext ctx) {
        String phoneNumber = ctx.request().getParam("phoneNumber");
        List<FileUpload> fileUploads = ctx.fileUploads();
        if (fileUploads.isEmpty()) {
            return ctx.response()
                    .setStatusCode(400)
                    .putHeader(CONTENT, JSON_TYPE)
                    .end(new JsonObject().put("error", "Driver's license image not uploaded").encode());
        } else {
            FileUpload licenseUpload = fileUploads.get(0);

            String originalFileName = licenseUpload.fileName();
            String uniqueFileName = userDal.generateUniqueFileName(originalFileName);

            return cloudinaryStorageConfig.uploadMediaAsset(userDal.readUploadedFile(licenseUpload, ctx.vertx()), uniqueFileName)
                    .flatMap(cloudinaryImageUrl -> userDal.updateUserDriversLicensePath(phoneNumber, cloudinaryImageUrl)
                            .flatMap(ignored -> {
                                JsonObject responseJson = new JsonObject().put("imagePath", cloudinaryImageUrl);
                                LOGGER.info("Driver's license image uploaded" + cloudinaryImageUrl);
                                return ctx.response()
                                        .setStatusCode(200)
                                        .putHeader(CONTENT, JSON_TYPE)
                                        .end(responseJson.encode());
                            }));
        }
    }
}
