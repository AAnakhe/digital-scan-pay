package com.ajavacode.digitalscanpay.model.dao;

import com.ajavacode.digitalscanpay.service.OtpUtil;
import com.ajavacode.digitalscanpay.model.dal.OtpDal;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class OtpDao implements OtpDal {


    private static final Logger LOGGER = LoggerFactory.getLogger(OtpDao.class);
    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final OtpUtil otpUtil;
    private final PgPool pgPool;

    public OtpDao(OtpUtil otpUtil, PgPool pgPool) {
        this.otpUtil = otpUtil;
        this.pgPool = pgPool;
    }

    @Override
    public Uni<Void> regenerateUserOtp(String phoneNumber, RoutingContext ctx) {

        return pgPool.withConnection(conn -> conn.prepare("SELECT uuid FROM Users WHERE phoneNumber = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(phoneNumber))
                        .flatMap(result -> {
                            Row row = result.iterator().next();
                            if (row == null) {
                                return ctx.response()
                                        .setStatusCode(404)
                                        .putHeader(CONTENT, JSON_TYPE)
                                        .end(new JsonObject().put("Error", "User not found").encode());
                            } else {
                                return conn.prepare("UPDATE Users SET otp = $1 WHERE phoneNumber = $2 RETURNING otp")
                                        .flatMap(insertStatement -> insertStatement
                                                .query()
                                                .execute(Tuple.of(otpUtil.generateOtp(otpUtil.generateSecretKey()), phoneNumber))
                                                .flatMap(updateResult -> {
                                                    String newOtpUser = updateResult.iterator().next().getString("otp");
                                                    LOGGER.info("New user otp regenerated");
                                                    return ctx.response()
                                                            .setStatusCode(200)
                                                            .putHeader(CONTENT, JSON_TYPE)
                                                            .end(new JsonObject().put("newOtp", newOtpUser).encode());
                                                }));
                            }
                        })));
    }


    @Override
    public Uni<Boolean> verifyUserOtp(String otp) {
        return pgPool
                .withTransaction(transaction -> transaction
                        .preparedQuery("SELECT o.otp FROM users o WHERE o.otp = $1")
                        .execute(Tuple.of(otp))
                        .onItem()
                        .transformToUni(result -> {
                            Row row = result.iterator().next();
                            if (row == null) {
                                LOGGER.info("User is invalid or expired");
                                return Uni.createFrom().nullItem();
                            }


                            return transaction
                                    .preparedQuery("UPDATE users SET isVerified = true WHERE otp = $1")
                                    .execute(Tuple.of(otp))
                                    .map(updateResult -> updateResult.rowCount() > 0);
                        }));
    }


    @Override
    public Uni<Boolean> verifyAgentOtp(String otp) {
        return pgPool
                .withTransaction(transaction -> transaction
                        .preparedQuery("SELECT otp FROM agent WHERE otp = $1")
                        .execute(Tuple.of(otp))
                        .onItem()
                        .transformToUni(result -> {
                            Row row = result.iterator().next();
                            if (row == null) {
                                return Uni.createFrom().item(false);
                            }

                            return transaction
                                    .preparedQuery("UPDATE agent SET isVerified = true WHERE otp = $1")
                                    .execute(Tuple.of(otp))
                                    .map(updateResult -> updateResult.rowCount() > 0);
                        }));
    }

    @Override
    public Uni<Void> regenerateAgentOtp(String phoneNumber, RoutingContext ctx) {

        return pgPool.withConnection(conn -> conn.prepare("SELECT uuid FROM agent WHERE phoneNumber = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(phoneNumber))
                        .flatMap(result -> {
                            Row row = result.iterator().next();
                            if (row == null) {
                                return ctx.response()
                                        .setStatusCode(404)
                                        .putHeader(CONTENT, JSON_TYPE)
                                        .end(new JsonObject().put("Error", "agent not found").encode());
                            } else {
                                return conn.prepare("UPDATE agent SET otp = $1 WHERE phoneNumber = $2 RETURNING otp")
                                        .flatMap(insertStatement -> insertStatement
                                                .query()
                                                .execute(Tuple.of(otpUtil.generateOtp(otpUtil.generateSecretKey()), phoneNumber))
                                                .flatMap(updateResult -> {
                                                    String newOtpAgent = updateResult.iterator().next().getString("otp");
                                                    LOGGER.info("New agent otp regenerated");
                                                    return ctx.response()
                                                            .setStatusCode(200)
                                                            .putHeader(CONTENT, JSON_TYPE)
                                                            .end(new JsonObject().put("newOtp", newOtpAgent).encode());
                                                }));
                            }
                        })));
    }

}
