package com.ajavacode.digitalscanpay.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

@Configuration
public class CloudinaryStorageConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudinaryStorageConfig.class);
    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryStorageConfig() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dwlhguyii",
                "api_key", "558159216332988",
                "api_secret", "iJfG1vQv9LirmiBmx4K7OY-7esI"
        ));
    }

    public Uni<String> uploadMediaAsset(Uni<byte[]> mediaData, String fileName) {
        return mediaData
                .flatMap(data -> Uni.createFrom().item(data)
                        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                        .map(d -> uploadToCloudinary(d, fileName))
                        .onFailure().recoverWithItem(error -> {
                            LOGGER.error("An error occurred while uploading media asset: {}", error.getMessage(), error);
                            return null;
                        })
                );
    }

    private String uploadToCloudinary(byte[] data, String fileName) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(data, ObjectUtils.asMap("public_id", fileName));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            LOGGER.error("An error occurred while uploading media asset: {}", e.getMessage(), e);
            return null;
        }
    }
}
