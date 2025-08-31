package com.cankolay.trash.server.common.util

import com.cankolay.trash.server.common.exception.ApiException
import com.cankolay.trash.server.common.model.ApiResponse
import com.cankolay.trash.server.common.service.I18nService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class SafeController(
    private val i18nService: I18nService
) {
    private val logger = LoggerFactory.getLogger(SafeController::class.java)

    operator fun <T> invoke(
        block: () -> ResponseEntity<ApiResponse<T>>,
    ): ResponseEntity<ApiResponse<T>> = try {
        block()
    } catch (e: ApiException) {
        ResponseEntity.status(e.status.value()).body(
            ApiResponse(
                message = i18nService.get(e.message),
                code = e.message,
            )
        )
    } catch (e: Exception) {
        logger.error(e.message, e)

        ResponseEntity.badRequest().body(
            ApiResponse(
                message = i18nService.get(e.message ?: "unknown_error"),
                code = e.message ?: "unknown_error",
            )
        )
    }
}