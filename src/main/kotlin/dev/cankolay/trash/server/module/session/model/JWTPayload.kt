package dev.cankolay.trash.server.module.session.model

data class JWTPayload(
    val id: String,
    val token: String,
)