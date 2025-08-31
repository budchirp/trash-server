package com.cankolay.trash.server.module.session.exception

import com.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class InvalidPasswordException : ApiException(status = HttpStatus.UNAUTHORIZED, message = "invalid_password")