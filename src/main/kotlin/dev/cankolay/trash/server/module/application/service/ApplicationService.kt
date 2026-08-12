package dev.cankolay.trash.server.module.application.service

import dev.cankolay.trash.server.module.application.entity.Application
import dev.cankolay.trash.server.module.application.exception.ApplicationNotFoundException
import dev.cankolay.trash.server.module.application.repository.ApplicationRepository
import dev.cankolay.trash.server.module.auth.service.AuthService
import dev.cankolay.trash.server.module.connection.repository.ConnectionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val connectionRepository: ConnectionRepository,
    private val auth: AuthService
) {
    @Transactional
    fun create(name: String, description: String, icon: String): Application {
        auth.validateSession()

        return applicationRepository.save(
            Application(
                name = name,
                description = description,
                icon = icon,
                owner = auth.user()
            )
        )
    }

    @Transactional(readOnly = true)
    fun getAll(): List<Application> {
        auth.validateSession()

        return applicationRepository.findAllByOwnerId(ownerId = auth.id())
    }

    @Transactional(readOnly = true)
    fun get(id: String): Application =
        applicationRepository.findById(id).orElseThrow { ApplicationNotFoundException() }

    @Transactional(readOnly = true)
    fun getOwned(id: String): Application =
        applicationRepository.findByIdAndOwnerId(id = id, ownerId = auth.id())
            ?: throw ApplicationNotFoundException()

    @Transactional
    fun delete(id: String) {
        auth.validateSession()

        val application = getOwned(id = id)
        connectionRepository.deleteAll(connectionRepository.findAllByApplicationId(applicationId = id))
        applicationRepository.delete(application)
    }
}
