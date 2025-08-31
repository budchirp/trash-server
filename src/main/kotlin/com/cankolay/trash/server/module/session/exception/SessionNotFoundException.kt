package com.cankolay.trash.server.module.session.exception

import com.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class SessionNotFoundException : ApiException(status = HttpStatus.NOT_FOUND, message = "session_not_found")