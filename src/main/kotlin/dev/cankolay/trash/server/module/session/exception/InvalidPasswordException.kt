package dev.cankolay.trash.server.module.session.exception

import dev.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class InvalidPasswordException :
    ApiException(status = HttpStatus.UNAUTHORIZED, code = "invalid_password", message = "Invalid password")