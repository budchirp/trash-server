package dev.cankolay.trash.server.common.exception

import org.springframework.http.HttpStatus

open class ApiException(
    val status: HttpStatus = HttpStatus.BAD_REQUEST,
    val code: String,
    override val message: String
) :
    RuntimeException(message)