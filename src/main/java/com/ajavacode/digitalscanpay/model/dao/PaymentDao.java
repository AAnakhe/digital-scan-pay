package com.ajavacode.digitalscanpay.model.dao;

import com.ajavacode.digitalscanpay.model.dal.PaymentDal;
import com.ajavacode.digitalscanpay.model.request.PaymentRequest;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Repository
public class PaymentDao implements PaymentDal {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDao.class);
    private final PgPool pgPool;

    public PaymentDao(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Uni<Boolean> initiatePayment(PaymentRequest paymentRequest) {
        return pgPool.withConnection(conn ->
                conn.prepare("SELECT u.uuid FROM Users u WHERE u.phonenumber = $1")
                        .flatMap(preparedStatement -> preparedStatement
                                .query()
                                .execute(Tuple.of(paymentRequest.getPhoneNumber())))
                        .flatMap(outerRowSet -> {
                            if (outerRowSet.rowCount() == 0) {
                                LOGGER.info("No user found for phoneNumber: {}", paymentRequest.getPhoneNumber());
                                return Uni.createFrom().item(false);
                            }

                            String userUuid = outerRowSet.iterator().next().getUUID("uuid").toString();

                            LocalDateTime localDateTime = LocalDateTime.now();
                            ZoneOffset zoneOffset = ZoneOffset.UTC;
                            OffsetDateTime offsetDateTime = localDateTime.atOffset(zoneOffset);

                            Tuple tuple = Tuple.tuple()
                                    .addValue(userUuid)
                                    .addValue(paymentRequest.getPlateNumber())
                                    .addValue(paymentRequest.getService())
                                    .addValue(paymentRequest.getPark())
                                    .addValue(paymentRequest.getAmount())
                                    .addValue(paymentRequest.getPlan())
                                    .addValue(offsetDateTime)
                                    .addValue("Successful");

                            return conn.prepare("INSERT INTO Payments (user_uuid, plate_number, service, park, amount, plan, payment_time, status) " +
                                            "VALUES ($1, $2, $3, $4, $5, $6, $7, $8)")
                                    .flatMap(preparedStatement -> preparedStatement
                                            .query()
                                            .execute(tuple)
                                            .flatMap(result -> {
                                                if (result.rowCount() != 1) {
                                                    LOGGER.error("Failed to insert payment for user UUID: {}", userUuid);
                                                    return Uni.createFrom().failure(new RuntimeException("Failed to insert payment"));
                                                }

                                                return conn.prepare("INSERT INTO Transaction (transaction_id, user_uuid, amount, transaction_time, service_type) " +
                                                                "VALUES ($1, $2, $3, $4, $5)")
                                                        .flatMap(insertStatement -> insertStatement
                                                                .query()
                                                                .execute(Tuple.of(
                                                                        UUID.randomUUID(),
                                                                        userUuid,
                                                                        paymentRequest.getAmount(),
                                                                        offsetDateTime,
                                                                        paymentRequest.getService()))
                                                        )
                                                        .map(rowSet -> {
                                                            LOGGER.info("Payment successfully inserted for user UUID: {}", userUuid);
                                                            return true;
                                                        });
                                            }));
                        })
        );
    }

}