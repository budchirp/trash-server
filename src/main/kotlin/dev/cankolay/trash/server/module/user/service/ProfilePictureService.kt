package dev.cankolay.trash.server.module.user.service

import dev.cankolay.trash.server.module.auth.service.AuthService
import dev.cankolay.trash.server.module.storage.PresignedUpload
import dev.cankolay.trash.server.module.storage.service.ObjectService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfilePictureService(
    private val auth: AuthService,
    private val objectService: ObjectService
) {
    @Transactional
    fun create(contentType: String, contentLength: Long? = null): PresignedUpload {
        val user = auth.user()

        val upload = objectService.createUpload(
            keyPrefix = "profiles/${user.id}/profile-picture",
            contentType = contentType,
            contentLength = contentLength
        )

        val oldPicture = user.profile.picture
        user.profile.picture = objectService.create(key = upload.key)
        objectService.delete(obj = oldPicture)

        return upload
    }

    @Transactional
    fun delete() {
        val user = auth.user()
        val oldPicture = user.profile.picture ?: return

        user.profile.picture = null
        objectService.delete(obj = oldPicture)
    }
}
