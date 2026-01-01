package dev.cankolay.trash.server.common.model

data class ApiResponse<T>(
    val error: Boolean = false,
    val code: String,
    val message: String = code,
    val data: T? = null
)