package com.ajavacode.digitalscanpay.model.dao;

import com.ajavacode.digitalscanpay.model.data.AgentSignUpData;
import com.ajavacode.digitalscanpay.model.data.LoginData;
import com.ajavacode.digitalscanpay.service.OtpUtil;
import com.ajavacode.digitalscanpay.model.Agent;
import com.ajavacode.digitalscanpay.model.dal.AgentDal;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Repository
public class AgentDao implements AgentDal {

    private static final String CONTENT = "content-type";
    private static final String JSON_TYPE = "application/json";
    private final OtpUtil otpUtil;
    private final PgPool pgPool;

    public AgentDao(OtpUtil otpUtil, PgPool pgPool) {
        this.otpUtil = otpUtil;
        this.pgPool = pgPool;
    }

    @Override
    public Uni<Void> addAgent(RoutingContext ctx, AgentSignUpData agentSignUpData) {

        String secretKey = otpUtil.generateSecretKey();
        String otp = otpUtil.generateOtp(secretKey);

        Agent agent = new Agent();

        if (Objects.equals(agentSignUpData.getPassword(), agentSignUpData.getConfirmPassword())) {
            agent.setName(agentSignUpData.getName());
            agent.setPhoneNumber(agentSignUpData.getPhoneNumber());
            agent.setGender(agentSignUpData.getGender());
            agent.setNin(agentSignUpData.getNin());
        } else {
            return Uni.createFrom().failure(new Throwable("Passwords do not match"));
        }

        String hashedPassword = BCrypt.hashpw(agentSignUpData.getPassword(), BCrypt.gensalt());

        List<String> roles = new ArrayList<>();
        roles.add("AGENT");
        agent.setRoles(roles);

        Tuple tuple = Tuple.tuple()
                .addValue(agent.getName())
                .addValue(agent.getPhoneNumber())
                .addValue(hashedPassword)
                .addValue(agent.getGender())
                .addValue(agent.getNin())
                .addValue(new JsonArray(roles))
                .addValue(agent.isVerified())
                .addValue(secretKey)
                .addValue(otp);

        return pgPool.withConnection(conn -> conn.prepare("SELECT a.phoneNumber FROM agent a WHERE a.phoneNumber = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(agent.getPhoneNumber()))
                        .onFailure().invoke(Throwable::printStackTrace)
                        .flatMap(result -> {
                            if (result.iterator().hasNext()) {
                                return ctx.response()
                                        .putHeader(CONTENT, JSON_TYPE)
                                        .setStatusCode(400)
                                        .end(new JsonObject().put("Oops!", "Phone number already registered").encode());
                            } else {
                                return conn.prepare("INSERT INTO agent(name, phoneNumber, password, gender, nin, roles, isVerified, secretKey, otp) " +
                                                "VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9) returning uuid")
                                        .flatMap(insertStatement -> insertStatement
                                                .query()
                                                .execute(tuple)
                                                .onFailure()
                                                .invoke(Throwable::printStackTrace)
                                                .flatMap(rows -> {
                                                    if (!rows.iterator().hasNext()) {
                                                        return ctx.response()
                                                                .putHeader(CONTENT, JSON_TYPE)
                                                                .end(String.valueOf(new JsonObject().put("Error", "Agent registration fail")));
                                                    } else {
                                                        return ctx.response()
                                                                .putHeader(CONTENT, JSON_TYPE)
                                                                .end(new JsonObject().put("otp", otpUtil.generateOtp(secretKey)).encode());
                                                    }
                                                }));
                            }
                        })));
    }

    @Override
    public Uni<Boolean> isValidCredentials(LoginData loginData) {
        return pgPool.withConnection(conn -> conn
                .prepare("SELECT p.phoneNumber, p.password FROM agent p WHERE p.phoneNumber = $1")
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
    public Uni<List<String>> getAgentRoles(String phoneNumber) {
        return pgPool.withConnection(conn -> conn
                .prepare("SELECT roles FROM agent WHERE phonenumber = $1")
                .flatMap(preparedStatement -> preparedStatement
                        .query()
                        .execute(Tuple.of(phoneNumber))
                        .map(rowSet -> {
                            Row row = rowSet.iterator().next();
                            JsonArray rolesArray = row.get(JsonArray.class, "roles");
                            return getStrings(rolesArray);
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
}
