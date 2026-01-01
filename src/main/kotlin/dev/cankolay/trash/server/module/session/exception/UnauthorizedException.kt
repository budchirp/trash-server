package dev.cankolay.trash.server.module.session.exception

import dev.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class UnauthorizedException :
    ApiException(status = HttpStatus.UNAUTHORIZED, code = "unauthorized", message = "Unauthorized access")