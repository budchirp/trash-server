package dev.cankolay.trash.server.module.storage.dto.response

import java.time.Instant

data class ObjectUploadResponseDto(
    val url: String,
    val key: String,
    val expiresAt: Instant
)
