package com.nistra.demy.platform.iam.domain.services;

import com.nistra.demy.platform.iam.domain.model.aggregates.User;
import com.nistra.demy.platform.iam.domain.model.commands.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;

public interface UserCommandService {

    Optional<ImmutablePair<User, String>> handle(SignInCommand command);

    Optional<User> handle(SignUpCommand command);

    Optional<ImmutablePair<User, String>> handle(VerifyUserCommand command);

    boolean handle(ResendVerificationCodeCommand command);

    void handle(AssignUserTenantId command);
}
