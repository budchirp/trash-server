package dev.cankolay.trash.server.module.storage.service

import dev.cankolay.trash.server.module.storage.ObjectStorage
import dev.cankolay.trash.server.module.storage.ObjectStorageConstants
import dev.cankolay.trash.server.module.storage.ObjectUploadRequest
import dev.cankolay.trash.server.module.storage.PresignedUpload
import dev.cankolay.trash.server.module.storage.entity.Object
import dev.cankolay.trash.server.module.storage.exception.FileTooLargeException
import dev.cankolay.trash.server.module.storage.exception.InvalidContentTypeException
import org.springframework.stereotype.Service

@Service
class ObjectService(
    private val objectStorage: ObjectStorage,
    private val keyGenerator: ObjectKeyGenerator
) {
    fun createUpload(keyPrefix: String, contentType: String, contentLength: Long? = null): PresignedUpload {
        val normalizedContentType = contentType.trim().lowercase()

        if (normalizedContentType !in ObjectStorageConstants.CONTENT_TYPES) {
            throw InvalidContentTypeException("Allowed content types: ${ObjectStorageConstants.CONTENT_TYPES.joinToString()}")
        }

        if (contentLength != null && contentLength > ObjectStorageConstants.MAX_FILE_SIZE) {
            throw FileTooLargeException("Maximum file size allowed is ${ObjectStorageConstants.MAX_FILE_SIZE} bytes")
        }

        val key = keyGenerator.generate(prefix = keyPrefix, contentType = normalizedContentType)

        return objectStorage.createUpload(
            ObjectUploadRequest(
                key = key,
                contentType = normalizedContentType,
                contentLength = contentLength,
                expiration = ObjectStorageConstants.UPLOAD_EXPIRATION
            )
        )
    }

    fun create(key: String): Object = Object(
        url = objectStorage.url(key = key).toString(),
        key = key
    )

    fun delete(obj: Object?) {
        obj?.let { objectStorage.delete(key = it.key) }
    }
}
