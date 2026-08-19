package dev.cankolay.trash.server.module.application.service

import dev.cankolay.trash.server.module.auth.service.AuthService
import dev.cankolay.trash.server.module.storage.PresignedUpload
import dev.cankolay.trash.server.module.storage.service.ObjectService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationIconService(
    private val auth: AuthService,
    private val applicationService: ApplicationService,
    private val objectService: ObjectService
) {
    @Transactional
    fun create(applicationId: String, contentType: String, contentLength: Long? = null): PresignedUpload {
        auth.validateSession()

        val application = applicationService.getOwned(id = applicationId)

        val upload = objectService.createUpload(
            keyPrefix = "applications/${application.id}/icon",
            contentType = contentType,
            contentLength = contentLength
        )

        val oldIcon = application.icon
        application.icon = objectService.create(key = upload.key)
        objectService.delete(obj = oldIcon)

        return upload
    }

    @Transactional
    fun delete(applicationId: String) {
        auth.validateSession()

        val application = applicationService.getOwned(id = applicationId)
        val oldIcon = application.icon ?: return

        application.icon = null
        objectService.delete(obj = oldIcon)
    }
}
