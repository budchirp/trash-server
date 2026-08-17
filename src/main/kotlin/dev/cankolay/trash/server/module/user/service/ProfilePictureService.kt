package dev.cankolay.trash.server.module.user.service

import dev.cankolay.trash.server.common.storage.ObjectStorage
import dev.cankolay.trash.server.common.storage.ObjectStorageConstants
import dev.cankolay.trash.server.common.storage.ObjectUploadRequest
import dev.cankolay.trash.server.common.storage.PresignedUpload
import dev.cankolay.trash.server.common.storage.exception.FileTooLargeException
import dev.cankolay.trash.server.common.storage.exception.InvalidContentTypeException
import dev.cankolay.trash.server.module.auth.service.AuthService
import dev.cankolay.trash.server.module.user.entity.Picture
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfilePictureService(
    private val auth: AuthService,
    private val objectStorage: ObjectStorage,
    private val keyGenerator: ProfilePictureKeyGenerator
) {
    @Transactional
    fun create(contentType: String, contentLength: Long? = null): PresignedUpload {
        val user = auth.user()

        val normalizedContentType = contentType.trim().lowercase()

        if (normalizedContentType !in ObjectStorageConstants.CONTENT_TYPES) {
            throw InvalidContentTypeException("Allowed content types: ${ObjectStorageConstants.CONTENT_TYPES.joinToString()}")
        }

        if (contentLength != null && contentLength > ObjectStorageConstants.MAX_FILE_SIZE) {
            throw FileTooLargeException("Maximum file size allowed is ${ObjectStorageConstants.MAX_FILE_SIZE} bytes")
        }

        val oldPicture = user.profile.picture
        val key = keyGenerator.generate(userId = user.id, contentType = normalizedContentType)

        val upload = objectStorage.createUpload(
            ObjectUploadRequest(
                key = key,
                contentType = normalizedContentType,
                contentLength = contentLength,
                expiration = ObjectStorageConstants.UPLOAD_EXPIRATION
            )
        )

        user.profile.picture = Picture(
            url = objectStorage.url(key = key).toString(),
            key = key
        )
        oldPicture?.let { objectStorage.delete(key = it.key) }

        return upload
    }

    @Transactional(readOnly = true)
    fun get(picture: Picture? = null): String? =
        (picture ?: auth.user().profile.picture)?.url

    @Transactional
    fun delete() {
        val user = auth.user()
        val oldPicture = user.profile.picture ?: return

        user.profile.picture = null
        objectStorage.delete(key = oldPicture.key)
    }
}
