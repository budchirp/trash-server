package dev.cankolay.trash.server.common.util

import dev.cankolay.trash.server.common.model.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class Controller {
    operator fun <T> invoke(
        block: () -> ResponseEntity<ApiResponse<T>>,
    ): ResponseEntity<ApiResponse<T>> = block()
}