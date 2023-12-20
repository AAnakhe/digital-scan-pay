package com.ajavacode.digitalscanpay.service;

import com.ajavacode.digitalscanpay.model.JwtUser;

public interface JwtBuilderHandler {
    String buildToken(JwtUser jwtUser);
}
