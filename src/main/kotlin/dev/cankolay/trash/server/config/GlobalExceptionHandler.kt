package dev.cankolay.trash.server.config

import dev.cankolay.trash.server.common.exception.ApiException
import dev.cankolay.trash.server.common.model.ApiResponse
import dev.cankolay.trash.server.common.service.I18nService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(private val i18nService: I18nService) {
    @ExceptionHandler(ApiException::class)
    fun handleRuntimeException(exception: ApiException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(exception.status)
            .body(
                ApiResponse(
                    error = true,
                    message = i18nService.getNullable(key = exception.code) ?: exception.message,
                    code = exception.code
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleAll(exception: Exception): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiResponse(
                    error = true,
                    message = i18nService.getNullable(key = exception.message ?: "internal_server_error")
                        ?: exception.message
                        ?: "Internal server error",
                    code = "internal_server_error"
                )
            )
    }
}