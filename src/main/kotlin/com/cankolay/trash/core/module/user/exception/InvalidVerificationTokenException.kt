package com.cankolay.trash.core.module.user.exception

import com.cankolay.trash.core.common.exception.ApiException
import org.springframework.http.HttpStatus

class InvalidVerificationTokenException :
    ApiException(status = HttpStatus.UNAUTHORIZED, message = "invalid_verification_token")