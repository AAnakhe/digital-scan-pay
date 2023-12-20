package com.ajavacode.digitalscanpay.config;

import com.ajavacode.digitalscanpay.model.Transaction;
import com.ajavacode.digitalscanpay.model.dal.TransactionDal;
import com.ajavacode.digitalscanpay.service.QrCodeService;
import com.ajavacode.digitalscanpay.model.User;
import com.ajavacode.digitalscanpay.model.dal.UserDal;
import com.ajavacode.digitalscanpay.util.QRCodeGenerator;
import io.smallrye.mutiny.Uni;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QrCodeConfig implements QrCodeService {

    private final UserDal userDal;
    private final TransactionDal transactionDal;
    private final QRCodeGenerator qrCodeGenerator;

    public QrCodeConfig(UserDal userDal, TransactionDal transactionDal, QRCodeGenerator qrCodeGenerator) {
        this.userDal = userDal;
        this.transactionDal = transactionDal;
        this.qrCodeGenerator = qrCodeGenerator;
    }

//    @Override
//    public Uni<UserQRCodeDetails> getUserAndUniqueID(String phoneNumber) {
//        return userDal.getUser(phoneNumber)
//                .flatMap(user -> {
//                    if (user == null) {
//                        return null;
//                    } else {
//                        return Uni.createFrom().item(new UserQRCodeDetails(user));
//                    }
//                });
//    }

    @Override
    public Uni<byte[]> generateQRCodeForUserAndTransaction(String phoneNumber) {
        return Uni.combine().all()
                .unis(userDal.getUser(phoneNumber), transactionDal.getMostRecentTransactionForQRCode(phoneNumber))
                .asTuple()
                .flatMap(tuple -> {
                    User user = tuple.getItem1();
                    Transaction mostRecentTransaction = tuple.getItem2();

                    String userData = formatUserData(user, mostRecentTransaction);
                    return Uni.createFrom().item(qrCodeGenerator.generateQRCode(userData));
                });
    }

    public String formatUserData(User user, Transaction mostRecentTransaction) {
        StringBuilder userData = new StringBuilder();
        userData.append("Name: ").append(user.getName()).append("\n");
        userData.append("Plate Number: ").append(user.getPlateNumber()).append("\n");

        if (mostRecentTransaction != null) {
            userData.append("Most Recent Transaction:\n");
            userData.append(formatTransaction(mostRecentTransaction)).append("\n");
        } else {
            userData.append("No recent transactions found for the user.");
        }

        return userData.toString();
    }

    public String formatTransaction(Transaction transaction) {
        return String.format("Service Type: %s\nAmount: %s\nTransaction Time: %s",
                transaction.getServiceType(), transaction.getAmount(), transaction.getTransactionTime());
    }
}