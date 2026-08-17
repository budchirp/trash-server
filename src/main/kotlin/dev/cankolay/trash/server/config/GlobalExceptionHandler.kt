package dev.cankolay.trash.server.config

import dev.cankolay.trash.server.common.exception.ApiException
import dev.cankolay.trash.server.common.model.ApiResponse
import dev.cankolay.trash.server.common.service.I18nService
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler(private val i18nService: I18nService) {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ApiException::class)
    fun handleApiException(exception: ApiException): ResponseEntity<ApiResponse<Nothing>> {
        if (exception.status.is5xxServerError) {
            logger.error("API error [${exception.code}]: ${exception.message}", exception)
        }

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

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(exception: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String?>>> {
        val errors = exception.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse(
                    error = true,
                    message = i18nService.get(key = "validation_failed"),
                    code = "validation_failed",
                    data = errors
                )
            )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMalformedJson(): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse(
                    error = true,
                    message = i18nService.get(key = "invalid_request"),
                    code = "invalid_request"
                )
            )
    }

    @ExceptionHandler(
        MethodArgumentTypeMismatchException::class,
        MissingServletRequestParameterException::class,
        ConstraintViolationException::class
    )
    fun handleInvalidRequest(): ResponseEntity<ApiResponse<Nothing>> =
        errorResponse(status = HttpStatus.BAD_REQUEST, code = "invalid_request")

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNotFound(): ResponseEntity<ApiResponse<Nothing>> =
        errorResponse(status = HttpStatus.NOT_FOUND, code = "not_found")

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(): ResponseEntity<ApiResponse<Nothing>> =
        errorResponse(status = HttpStatus.METHOD_NOT_ALLOWED, code = "method_not_allowed")

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleConflict(): ResponseEntity<ApiResponse<Nothing>> =
        errorResponse(status = HttpStatus.CONFLICT, code = "conflict")

    @ExceptionHandler(Exception::class)
    fun handleAll(exception: Exception): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Unhandled exception", exception)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiResponse(
                    error = true,
                    message = i18nService.get(key = "internal_server_error"),
                    code = "internal_server_error"
                )
            )
    }

    private fun errorResponse(status: HttpStatus, code: String): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(status)
            .body(
                ApiResponse(
                    error = true,
                    message = i18nService.get(key = code),
                    code = code
                )
            )
}
