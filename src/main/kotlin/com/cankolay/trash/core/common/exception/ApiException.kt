package com.cankolay.trash.core.common.exception

import org.springframework.http.HttpStatus

open class ApiException(val status: HttpStatus = HttpStatus.BAD_REQUEST, override val message: String) :
    IllegalStateException(message)