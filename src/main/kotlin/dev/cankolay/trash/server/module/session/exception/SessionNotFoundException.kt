package dev.cankolay.trash.server.module.session.exception

import dev.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class SessionNotFoundException :
    ApiException(status = HttpStatus.NOT_FOUND, code = "session_not_found", message = "Session not found")