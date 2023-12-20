package com.ajavacode.digitalscanpay.model.dal;

import com.ajavacode.digitalscanpay.model.data.JwtSignUpData;
import com.ajavacode.digitalscanpay.model.JwtUser;
import io.smallrye.mutiny.Uni;

public interface JwtUserDal {
    Uni<String> add(JwtSignUpData jwtSignUpData);

    Uni<JwtUser> verifyUser(String token);
}
