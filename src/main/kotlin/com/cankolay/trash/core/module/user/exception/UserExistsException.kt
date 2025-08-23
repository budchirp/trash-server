package com.cankolay.trash.core.module.user.exception

import com.cankolay.trash.core.common.exception.ApiException
import org.springframework.http.HttpStatus

class UserExistsException : ApiException(status = HttpStatus.CONFLICT, message = "user_exists")