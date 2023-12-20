package com.ajavacode.digitalscanpay.model.dao;

import com.ajavacode.digitalscanpay.model.data.JwtSignUpData;
import com.ajavacode.digitalscanpay.service.JwtBuilderHandler;
import com.ajavacode.digitalscanpay.model.JwtUser;
import com.ajavacode.digitalscanpay.model.dal.JwtUserDal;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class JwtUserDao implements JwtUserDal {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUserDao.class);

    private final JwtBuilderHandler jwtBuilderHandler;

    private final PgPool pgPool;

    public JwtUserDao(PgPool pgPool, JwtBuilderHandler jwtBuilderHandler) {
        this.pgPool = pgPool;
        this.jwtBuilderHandler = jwtBuilderHandler;
    }

    //    private final PasswordHashingHandler encode;


    @Override
    public Uni<String> add(JwtSignUpData jwtSignUpData) {
        JwtUser jwtUser = new JwtUser();

        if (Objects.equals(jwtSignUpData.getPassword(), jwtSignUpData.getConfirmPassword())) {
            jwtUser.setPhoneNumber(jwtSignUpData.getPhoneNumber());
            jwtUser.setPassword(jwtSignUpData.getPassword());
            jwtUser.setNin(jwtSignUpData.getNin());
            jwtUser.setVehicleType(jwtSignUpData.getVehicleType());
            jwtUser.setManufacturer(jwtSignUpData.getManufacturer());
            jwtUser.setPlateNumber(jwtSignUpData.getPlateNumber());
            jwtUser.setVerified(false);
        } else {
            return Uni.createFrom().failure(new Throwable("Passwords do not match"));
        }

        String jwtToken = jwtBuilderHandler.buildToken(jwtUser);


        Tuple tuple = Tuple.tuple()
                .addValue(jwtUser.getPhoneNumber())
                .addValue(jwtUser.getPassword())
                .addValue(jwtUser.getNin())
                .addValue(jwtUser.getVehicleType())
                .addValue(jwtUser.getManufacturer())
                .addValue(jwtUser.getPlateNumber())
                .addValue(jwtToken)
                .addValue(jwtUser.isVerified());

        return pgPool.withConnection(conn -> conn.prepare("SELECT COUNT(*) FROM Users WHERE phoneNumber = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(jwtUser.getPhoneNumber()))
                        .onFailure().invoke(Throwable::printStackTrace)
                        .flatMap(result -> {
                            Row row = result.iterator().next();
                            int count = row.getInteger(0);
                            if (count > 0) {
                                return Uni.createFrom().failure(new Throwable("Phone number already registered"));
                            } else {
                                return conn.prepare("INSERT INTO Users(phoneNumber, password, nin, vehicleType, manufacturer, " +
                                                "plateNumber, verification_token, isVerified) VALUES ($1, $2, $3, $4, $5, $6, $7, $8) returning uuid")
                                        .flatMap(insertStatement -> insertStatement
                                                .query()
                                                .execute(tuple)
                                                .onFailure()
                                                .invoke(Throwable::printStackTrace)
                                                .flatMap(rows -> {
                                                    if (rows == null || !rows.iterator().hasNext()) {
                                                        return Uni.createFrom().failure(new Throwable("User registration failed"));
                                                    } else {
                                                        // Registration successful, generate and return a JWT token

                                                        return Uni.createFrom().item(jwtToken);
                                                    }
                                                }));
                            }
                        })));
    }

    @Override
    public Uni<JwtUser> verifyUser(String token) {
        return pgPool.withConnection(conn -> conn.prepare("SELECT * FROM Users WHERE verification_token = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(token))
                        .flatMap(result -> {
                            Row row = result.iterator().next();
                            if (row != null) {

                                return conn.prepare("UPDATE Users SET isVerified = true WHERE phonenumber = $1")
                                        .flatMap(updateStatement -> updateStatement
                                                .query()
                                                .execute(Tuple.of(row.getString("phonenumber")))
                                                .map(updatedRows -> {
                                                    if (updatedRows.rowCount() == 1) {
                                                        // Update successful, return the user
                                                        JwtUser jwtUser;
                                                        jwtUser = new JwtUser(
                                                                row.getString("phonenumber"),
                                                                row.getString("nin"),
                                                                row.getString("vehicletype"),
                                                                row.getString("manufacturer"),
                                                                row.getString("platenumber"),
                                                                true
                                                        );
                                                        LOGGER.info("user Dao is verified");
                                                        return jwtUser;
                                                    } else {
                                                        LOGGER.warn("User verification update failed for token: {}", token);
                                                        return null;
                                                    }
                                                })
                                        );
                            } else {
                                LOGGER.warn("User not found for token: {}", token);
                                return Uni.createFrom().nullItem();
                            }
                        })
                ));
    }

}
