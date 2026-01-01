package dev.cankolay.trash.server.module.session.exception

import dev.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class UserNotFoundException :
    ApiException(status = HttpStatus.UNAUTHORIZED, code = "user_not_found", message = "user_not_found")