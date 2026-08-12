package dev.cankolay.trash.server.common.model

import dev.cankolay.trash.server.module.auth.entity.TokenType

enum class JwtPurpose {
    ACCESS,
    SECURITY
}

data class JWTPayload(
    val user: String,
    val id: String?,
    val type: TokenType?,
    val purpose: JwtPurpose,
)
