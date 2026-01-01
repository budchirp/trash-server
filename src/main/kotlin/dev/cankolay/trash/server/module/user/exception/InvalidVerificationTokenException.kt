package dev.cankolay.trash.server.module.user.exception

import dev.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class InvalidVerificationTokenException :
    ApiException(
        status = HttpStatus.UNAUTHORIZED,
        code = "invalid_verification_token",
        message = "Invalid verification token"
    )