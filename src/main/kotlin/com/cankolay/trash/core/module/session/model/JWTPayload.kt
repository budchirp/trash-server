package com.cankolay.trash.core.module.session.model

data class JWTPayload(
    val id: Long,
    val token: String,
)