package com.ajavacode.digitalscanpay.handler;



import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.auth.impl.UserImpl;
import io.vertx.mutiny.ext.auth.User;
import io.vertx.mutiny.ext.auth.authentication.AuthenticationProvider;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider extends AuthenticationProvider {


    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
    private final PgPool pgPool;

    public CustomAuthenticationProvider(io.vertx.ext.auth.authentication.AuthenticationProvider delegate, PgPool pgPool) {
        super(delegate);
        this.pgPool = pgPool;
    }



    public void authenticate(JsonObject jsonObject, Handler<AsyncResult<User>> handler) {

        String phoneNumber = jsonObject.getString("phoneNumber");
        String password = jsonObject.getString("password");

        pgPool.withConnection(conn ->
                conn.prepare("SELECT p.phoneNumber, p.password FROM Users p WHERE p.phoneNumber = $1 AND p.password = $2")
                        .flatMap(preparedStatement ->
                                preparedStatement
                                        .query()
                                        .execute(Tuple.of(phoneNumber, password))
                        )
                        .map(rowSet -> {
                            if (rowSet.rowCount() == 1) {
                                return rowSet.iterator().next();
                            }
                            return null;
                        })
        )
                .subscribe()
                .with(
                row -> {
                    if (row != null) {
                        User user = User.newInstance(new UserImpl());

                        LOGGER.info("User Logged in");
                        handler.handle(Future.succeededFuture(user));
                    } else {
                        LOGGER.info("Invalid credentials");
                        handler.handle(Future.failedFuture("Invalid credentials"));
                    }
                },
                error -> {
                    LOGGER.error("Authentication failed with error: " + error.getMessage());
                    handler.handle(Future.failedFuture(error.getMessage()));
                }
        );
    }
}
