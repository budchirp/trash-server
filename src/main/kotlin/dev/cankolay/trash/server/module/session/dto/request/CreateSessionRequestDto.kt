package dev.cankolay.trash.server.module.session.dto.request

data class CreateSessionRequestDto(
    val username: String,
    val password: String
)
