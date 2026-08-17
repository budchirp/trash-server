package dev.cankolay.trash.server.common.storage

import java.net.URI

interface ObjectStorage {

    fun createUpload(
        request: ObjectUploadRequest
    ): PresignedUpload

    fun url(key: String): URI

    fun delete(key: String)
}
