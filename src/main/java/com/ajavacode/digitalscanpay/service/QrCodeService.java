package com.ajavacode.digitalscanpay.service;

import io.smallrye.mutiny.Uni;

public interface QrCodeService {
//    Uni<UserQRCodeDetails> getUserAndUniqueID(String phoneNumber);

    Uni<byte[]> generateQRCodeForUserAndTransaction(String phoneNumber);
}
