package com.cankolay.trash.server.module.user.exception

import com.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class UserExistsException : ApiException(status = HttpStatus.CONFLICT, message = "user_exists")