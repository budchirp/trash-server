package dev.cankolay.trash.server.module.application.service

import dev.cankolay.trash.server.module.application.entity.Application
import dev.cankolay.trash.server.module.application.exception.ApplicationNotFoundException
import dev.cankolay.trash.server.module.application.exception.DeveloperProgramRequiredException
import dev.cankolay.trash.server.module.application.repository.ApplicationRepository
import dev.cankolay.trash.server.module.auth.service.AuthService
import dev.cankolay.trash.server.module.connection.repository.ConnectionRepository
import dev.cankolay.trash.server.module.storage.service.ObjectService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val connectionRepository: ConnectionRepository,
    private val auth: AuthService,
    private val objectService: ObjectService
) {
    @Transactional
    fun create(name: String, description: String): Application {
        auth.validateSession()

        val user = auth.user()
        if (!user.profile.dev) {
            throw DeveloperProgramRequiredException()
        }

        return applicationRepository.save(
            Application(
                name = name,
                description = description,
                owner = user
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
        objectService.delete(obj = application.icon)
        applicationRepository.delete(application)
    }
}
