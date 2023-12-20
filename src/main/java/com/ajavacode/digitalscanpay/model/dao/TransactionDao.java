package com.ajavacode.digitalscanpay.model.dao;

import com.ajavacode.digitalscanpay.model.dal.TransactionDal;
import com.ajavacode.digitalscanpay.model.Transaction;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionDao implements TransactionDal {

    private static final Logger LOGGER = LoggerFactory.getLogger(Transaction.class);
    private final PgPool pgPool;

    public TransactionDao(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Uni<List<Transaction>> getRecentTransactionsByPhoneNumber(String phoneNumber) {
        return pgPool.withConnection(conn ->
                conn.prepare("SELECT u.uuid FROM Users u WHERE u.phoneNumber = $1")
                        .flatMap(preparedStatement -> preparedStatement
                                .query()
                                .execute(Tuple.of(phoneNumber)))
                        .flatMap(rowSet -> {
                            if (rowSet.rowCount() == 0) {
                                LOGGER.info("No user found for phoneNumber: {}", phoneNumber);
                                return Uni.createFrom().nullItem();
                            }

                            String userUuid = rowSet.iterator().next().getUUID("uuid").toString();
                            LOGGER.info("User found. UUID: {}", userUuid);

                            return conn.prepare("SELECT u.transaction_id, u.service_type, u.amount, u.transaction_time FROM Transaction u WHERE u.user_uuid = $1 ORDER BY transaction_time DESC LIMIT 10")
                                    .flatMap(retrieveTransaction -> retrieveTransaction
                                            .query()
                                            .execute(Tuple.of(userUuid))
                                            .map(rowSet2 -> {
                                                var recentTransactions = new ArrayList<Transaction>();
                                                for (Row row : rowSet2) {
                                                    var transaction = new Transaction();
                                                    transaction.setTransactionId(row.getUUID("transaction_id"));
                                                    transaction.setUserUuid(UUID.fromString(userUuid));
                                                    transaction.setServiceType(row.getString("service_type"));
                                                    transaction.setAmount(row.getBigDecimal("amount"));
                                                    transaction.setTransactionTime(row.getLocalDateTime("transaction_time"));
                                                    recentTransactions.add(transaction);
                                                }
                                                LOGGER.info("Recent transactions retrieved for userUuid: {}", userUuid);
                                                return recentTransactions;
                                            })
                                    );
                        })
        );
    }

    @Override
    public Uni<Transaction> getMostRecentTransactionForQRCode(String phoneNumber) {
        return pgPool.withConnection(conn ->
                conn.prepare("SELECT u.uuid FROM Users u WHERE u.phoneNumber = $1")
                        .flatMap(preparedStatement -> preparedStatement
                                .query()
                                .execute(Tuple.of(phoneNumber)))
                        .flatMap(rowSet -> {
                            if (rowSet.rowCount() == 0) {
                                LOGGER.info("No user found for phoneNumber: {}", phoneNumber);
                                return Uni.createFrom().nullItem();
                            }

                            String userUuid = rowSet.iterator().next().getUUID("uuid").toString();
                            LOGGER.info("User found. UUID: {}", userUuid);

                            return conn.prepare("SELECT u.transaction_id, u.service_type, u.amount, u.transaction_time " +
                                            "FROM Transaction u " +
                                            "WHERE u.user_uuid = $1 " +
                                            "ORDER BY transaction_time DESC " +
                                            "LIMIT 1")  // Only retrieve the most recent transaction
                                    .flatMap(retrieveTransaction -> retrieveTransaction
                                            .query()
                                            .execute(Tuple.of(userUuid))
                                            .map(rowSet2 -> {
                                                if (rowSet2.rowCount() == 0) {

                                                    return null;
                                                }

                                                Row row = rowSet2.iterator().next();
                                                var transaction = new Transaction();
                                                transaction.setTransactionId(row.getUUID("transaction_id"));
                                                transaction.setUserUuid(UUID.fromString(userUuid));
                                                transaction.setServiceType(row.getString("service_type"));
                                                transaction.setAmount(row.getBigDecimal("amount"));
                                                transaction.setTransactionTime(row.getLocalDateTime("transaction_time"));
                                                LOGGER.info("Most recent transaction for QRCode retrieved for userUuid: {}", userUuid);
                                                return transaction;
                                            }));
                        })
        );
    }

}
