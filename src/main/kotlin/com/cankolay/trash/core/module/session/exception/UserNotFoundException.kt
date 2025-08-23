package com.cankolay.trash.core.module.session.exception

import com.cankolay.trash.core.common.exception.ApiException
import org.springframework.http.HttpStatus

class UserNotFoundException : ApiException(status = HttpStatus.UNAUTHORIZED, message = "user_not_found")