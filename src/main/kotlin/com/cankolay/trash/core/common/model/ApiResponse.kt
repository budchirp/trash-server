package com.cankolay.trash.core.common.model

data class ApiResponse<T>(
    val message: String,
    val code: String,
    val data: T? = null
)