package dev.cankolay.trash.server.module.user.exception

import dev.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class UserExistsException :
    ApiException(status = HttpStatus.CONFLICT, code = "user_exists", message = "User already exists")