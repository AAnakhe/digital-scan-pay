package com.ajavacode.digitalscanpay.model.dao;

import com.ajavacode.digitalscanpay.model.data.LoginData;
import com.ajavacode.digitalscanpay.service.OtpUtil;
import com.ajavacode.digitalscanpay.model.User;
import com.ajavacode.digitalscanpay.model.dal.UserDal;
import com.ajavacode.digitalscanpay.model.data.UserSignUpData;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.FileUpload;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserDao implements UserDal {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);
    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final OtpUtil otpUtil;
    private final PgPool pgPool;

    public UserDao(PgPool pgPool, OtpUtil otpUtil, Vertx vertx) {
        this.pgPool = pgPool;
        this.otpUtil = otpUtil;
    }

    @Override
    public Uni<User> getUser(String phoneNumber) {

        return pgPool.withConnection(conn -> conn.prepare("SELECT uuid, name, phoneNumber, email, password, unique_id, gender, nin, vehicleType, manufacturer, plateNumber, drivers_license, roles, isVerified, secretKey, otp FROM Users WHERE phoneNumber = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(phoneNumber))
                        .flatMap(rowSet -> {

                            if (rowSet.rowCount() == 0){
                                LOGGER.info("No user found for phoneNumber: {}", phoneNumber);
                                return Uni.createFrom().nullItem();
                            } else {

                                Row row = rowSet.iterator().next();
                                User user = new User();
                                user.setUuid(row.getUUID("uuid").toString());
                                user.setName(row.getString("name"));
                                user.setPhoneNumber(row.getString("phonenumber"));
                                user.setEmail(row.getString("email"));
                                user.setPassword(row.getString("password"));
                                user.setUniqueID(row.getString("unique_id"));
                                user.setGender(row.getString("gender"));
                                user.setNin(row.getString("nin"));
                                user.setVehicleType(row.getString("vehicletype"));
                                user.setManufacturer(row.getString("manufacturer"));
                                user.setPlateNumber(row.getString("platenumber"));
                                user.setDriversLicense(row.getString("drivers_license"));

                                JsonArray rolesArray = row.get(JsonArray.class, "roles");
                                List<String> roles = getStrings(rolesArray);

                                user.setRoles(roles);
                                user.setVerified(row.getBoolean("isverified"));
                                user.setSecretKey(row.getString("secretkey"));
                                user.setOtp(row.getString("otp"));

                                LOGGER.info("User details retrieved");
                                return Uni.createFrom().item(user);
                            }
                        })));
    }

    private static List<String> getStrings(JsonArray rolesArray) {
        List<String> roles = new ArrayList<>();

        if (rolesArray != null) {
            for (Object role : rolesArray.getList()) {
                if (role instanceof String) {
                    roles.add((String) role);
                }
            }
        } else {

            roles = Collections.emptyList();
        }
        return roles;
    }

    @Override
    public Uni<List<String>> getUserRoles(String phoneNumber) {
        return pgPool.withConnection(conn -> conn
                .prepare("SELECT roles FROM users WHERE phonenumber = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(phoneNumber))
                        .map(rowSet -> {
                            Row row = rowSet.iterator().next();
                            JsonArray rolesArray = row.get(JsonArray.class, "roles");
                            return getStrings(rolesArray);
                        })));
    }

    @Override
    public Uni<JsonObject> getUserAndUniqueID(String phoneNumber) {
        return pgPool.withConnection(conn -> conn.prepare("SELECT name, unique_id, plateNumber FROM Users WHERE phoneNumber = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(phoneNumber))
                        .map(rowSet -> {
                            if (rowSet.rowCount() == 0) {
                                LOGGER.info("No user found for phoneNumber: {}", phoneNumber);
                                return new JsonObject().put("error", "no user found");
                            } else {
                                Row row = rowSet.iterator().next();
                                return new JsonObject()
                                        .put("name", row.getString("name"))
                                        .put("plateNumber", row.getString("platenumber"))
                                        .put("uniqueId", row.getString("unique_id"));

                            }
                        })));
    }


    @Override
    public Uni<Void> add(RoutingContext ctx, UserSignUpData signUpData) {

        String secretKey = otpUtil.generateSecretKey();
        String otp = otpUtil.generateOtp(secretKey);
        String uniqueID = otpUtil.generateUniqueID();


        User user = new User();

        if (Objects.equals(signUpData.getPassword(), signUpData.getConfirmPassword())) {
            user.setName(signUpData.getName());
            user.setPhoneNumber(signUpData.getPhoneNumber());
            user.setEmail(signUpData.getEmail());
            user.setNin(signUpData.getNin());
            user.setGender(signUpData.getGender());
            user.setVehicleType(signUpData.getVehicleType());
            user.setManufacturer(signUpData.getManufacturer());
            user.setPlateNumber(signUpData.getPlateNumber());
            user.setVerified(false);
        } else {
            return Uni.createFrom().failure(new Throwable("Passwords do not match"));
        }

        String hashedPassword = BCrypt.hashpw(signUpData.getPassword(), BCrypt.gensalt());

        List<String> roles = new ArrayList<>();
        roles.add("USER");
        user.setRoles(roles);

        Tuple tuple = Tuple.tuple()
                .addValue(user.getName())
                .addValue(user.getPhoneNumber())
                .addValue(user.getEmail())
                .addValue(hashedPassword)
                .addValue(uniqueID)
                .addValue(user.getGender())
                .addValue(user.getNin())
                .addValue(user.getVehicleType())
                .addValue(user.getManufacturer())
                .addValue(user.getPlateNumber())
                .addValue(new JsonArray(roles))
                .addValue(user.isVerified())
                .addValue(secretKey)
                .addValue(otp);

        return pgPool.withConnection(conn -> conn.prepare("SELECT p.phoneNumber FROM Users p WHERE p.phoneNumber = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(user.getPhoneNumber()))
                        .onFailure().invoke(Throwable::printStackTrace)
                        .flatMap(result -> {
                            if (result.iterator().hasNext()) {
                                LOGGER.info("Phone number already registered");
                                return ctx.response()
                                        .putHeader(CONTENT, JSON_TYPE)
                                        .setStatusCode(400)
                                        .end(new JsonObject().put("Oops!", "Phone number already registered").encode());
                            } else {
                                return conn.prepare("INSERT INTO Users(name, phoneNumber, email, password, unique_id, gender, nin, vehicleType, manufacturer, " +
                                                "plateNumber, roles, isVerified, secretKey, otp) " +
                                                "VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14) returning uuid")
                                        .flatMap(insertStatement -> insertStatement
                                                .query()
                                                .execute(tuple)
                                                .onFailure()
                                                .invoke(Throwable::printStackTrace)
                                                .flatMap(rows -> {
                                                    if (!rows.iterator().hasNext()) {
                                                        LOGGER.info("User registration fail");
                                                        return ctx.response()
                                                                .putHeader(CONTENT, JSON_TYPE)
                                                                .end(String.valueOf(new JsonObject().put("Error", "User registration fail")));
                                                    } else {
                                                        LOGGER.info("User otp generated");
                                                        return ctx.response()
                                                                .putHeader(CONTENT, JSON_TYPE)
                                                                .end(new JsonObject().put("token", otpUtil.generateOtp(secretKey))
                                                                        .put("uniqueID", uniqueID).encode());
                                                    }

                                                }));

                            }
                        })));
    }


    @Override
    public Uni<Boolean> isValidCredentials(LoginData loginData) {
        return pgPool.withConnection(conn -> conn
                .prepare("SELECT p.phoneNumber, p.password FROM Users p WHERE p.phoneNumber = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(loginData.getPhoneNumber()))
                        .map(rowSet -> {
                            if (rowSet.rowCount() != 1) {
                                return false;
                            }

                            Row row = rowSet.iterator().next();
                            String hashedPassword = row.getString("password");

                            return BCrypt.checkpw(loginData.getPassword(), hashedPassword);
                        })
                )
        );
    }

    @Override
    public Uni<Void> updateUserDriversLicensePath(String phoneNumber, String imageFilePath) {
        return pgPool.withConnection(conn -> conn
                .prepare("UPDATE Users SET drivers_license = $1 WHERE phoneNumber = $2")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(imageFilePath, phoneNumber))
                        .onFailure().invoke(error -> {
                            LOGGER.error("Failed to update driver's license path: " + error.getMessage(), error);
                        }))
                .replaceWithVoid());
    }


    @Override
    public Uni<byte[]> readUploadedFile(FileUpload uploadedFile, Vertx vertx) {
        return vertx.fileSystem()
                .readFile(uploadedFile.uploadedFileName())
                .onItem()
                .transformToUni(buffer -> Uni.createFrom().item(buffer.getBytes()));
    }

    @Override
    public String generateUniqueFileName(String originalFileName) {
        return originalFileName + "_" + System.currentTimeMillis();
    }
}
