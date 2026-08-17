package dev.cankolay.trash.server.common.storage

import java.time.Duration

data class ObjectUploadRequest(
    val key: String,

    val contentType: String,
    val contentLength: Long? = null,

    val expiration: Duration
)
