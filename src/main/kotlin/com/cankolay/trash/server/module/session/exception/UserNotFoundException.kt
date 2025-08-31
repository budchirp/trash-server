package com.cankolay.trash.server.module.session.exception

import com.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class UserNotFoundException : ApiException(status = HttpStatus.UNAUTHORIZED, message = "user_not_found")