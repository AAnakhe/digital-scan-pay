package com.ajavacode.digitalscanpay.model.dal;

import com.ajavacode.digitalscanpay.model.data.AgentSignUpData;
import com.ajavacode.digitalscanpay.model.data.LoginData;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.ext.web.RoutingContext;

import java.util.List;

public interface AgentDal {
    Uni<Void> addAgent(RoutingContext ctx, AgentSignUpData agentSignUpData);
    Uni<Boolean> isValidCredentials(LoginData loginData);

    Uni<List<String>> getAgentRoles(String phoneNumber);
}
