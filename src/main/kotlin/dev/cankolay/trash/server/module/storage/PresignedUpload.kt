package dev.cankolay.trash.server.module.storage

import java.net.URI
import java.time.Instant

data class PresignedUpload(
    val url: URI,
    val key: String,

    val expiresAt: Instant
)
