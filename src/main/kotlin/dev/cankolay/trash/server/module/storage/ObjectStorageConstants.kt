package dev.cankolay.trash.server.module.storage

import java.time.Duration

object ObjectStorageConstants {
    const val MAX_FILE_SIZE: Long = 5 * 1024 * 1024
    val CONTENT_TYPES: Set<String> = setOf("image/jpeg", "image/jpg", "image/png", "image/webp")

    val UPLOAD_EXPIRATION: Duration = Duration.ofMinutes(15)
}
