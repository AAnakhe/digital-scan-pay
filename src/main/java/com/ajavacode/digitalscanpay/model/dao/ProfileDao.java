package com.ajavacode.digitalscanpay.model.dao;

import com.ajavacode.digitalscanpay.model.dal.ProfileDal;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileDao implements ProfileDal {

    private final PgPool pgPool;

    public ProfileDao(PgPool pgPool) {
        this.pgPool = pgPool;
    }


    @Override
    public Uni<JsonObject> getUserProfile(String phoneNumber) {
        return pgPool.withConnection(conn -> conn.prepare("SELECT u.name, u.phoneNumber, u.email, u.unique_id, u.gender, u.vehicleType, " +
                        "u.manufacturer, u.plateNumber, u.drivers_license FROM Users u WHERE u.phoneNumber = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(phoneNumber))
                        .map(rowSet -> {
                            if (rowSet.size() == 1) {
                                Row row = rowSet.iterator().next();
                                return new JsonObject()
                                        .put("name", row.getString("name"))
                                        .put("phoneNumber", row.getString("phonenumber"))
                                        .put("email", row.getString("email"))
                                        .put("uniqueID", row.getString("unique_id"))
                                        .put("gender", row.getString("gender"))
                                        .put("vehicleType", row.getString("vehicletype"))
                                        .put("manufacturer", row.getString("manufacturer"))
                                        .put("plateNumber", row.getString("platenumber"))
                                        .put("drivers_license", row.getString("drivers_license"));
                            } else {
                                return new JsonObject().put("error", "User not found");
                            }
                        })));
    }
}