package dev.cankolay.trash.server.module.user.dto.response

import java.time.Instant

data class ProfilePictureUploadResponseDto(
    val url: String,
    val key: String,
    val expiresAt: Instant
)
