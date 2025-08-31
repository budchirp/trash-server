package com.cankolay.trash.server.common.model

data class ApiResponse<T>(
    val message: String,
    val code: String,
    val data: T? = null
)