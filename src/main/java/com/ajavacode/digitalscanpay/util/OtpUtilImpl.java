package com.ajavacode.digitalscanpay.util;

import com.ajavacode.digitalscanpay.service.OtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;


@Service
public class OtpUtilImpl implements OtpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtpUtilImpl.class);
    @Override
    public String generateOtp(String secretKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);

            long timestamp = System.currentTimeMillis() / 1000 / 30;

            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(timestamp);
            byte[] timestampBytes = buffer.array();

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);

            byte[] hashBytes = mac.doFinal(timestampBytes);

            int offset = hashBytes[hashBytes.length - 1] & 0x0F;
            int otpValue = (
                    ((hashBytes[offset] & 0x7F) << 24) |
                            ((hashBytes[offset + 1] & 0xFF) << 16) |
                            ((hashBytes[offset + 2] & 0xFF) << 8) |
                            (hashBytes[offset + 3] & 0xFF)
            ) % 1000000; // 6-digit OTP

            return String.format("%06d", otpValue);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.error("Invalid key exception", e);
            return null;
        }
    }
    @Override
    public String generateSecretKey() {
        int keyLengthBytes = 20;

        byte[] keyBytes = new byte[keyLengthBytes];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(keyBytes);

        return Base64.getEncoder().encodeToString(keyBytes);
    }


    @Override
    public String generateUniqueID() {
        Random random = new Random();
        int min = 10000000;
        int max = 99999999;
        int randomNumber = random.nextInt(max - min + 1) + min;
        return String.format("%08d", randomNumber);
    }
}
