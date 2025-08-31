package com.cankolay.trash.server.module.user.exception

import com.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class InvalidVerificationTokenException :
    ApiException(status = HttpStatus.UNAUTHORIZED, message = "invalid_verification_token")