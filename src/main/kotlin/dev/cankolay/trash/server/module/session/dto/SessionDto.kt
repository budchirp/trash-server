package dev.cankolay.trash.server.module.session.dto

data class SessionDto(
    val token: String,

    val ip: String,

    val device: String,
    val platform: String,

    val os: String,
    val browser: String,

    val expiresAt: String
)
