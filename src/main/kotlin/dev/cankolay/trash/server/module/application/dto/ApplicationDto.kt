package dev.cankolay.trash.server.module.application.dto

import java.time.Instant

data class ApplicationDto(
    val id: String,

    val name: String,
    val description: String,

    val icon: String?,

    val createdAt: Instant?
)
