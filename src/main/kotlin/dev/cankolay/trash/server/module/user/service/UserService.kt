package dev.cankolay.trash.server.module.user.service

import dev.cankolay.trash.server.common.exception.RateLimitedException
import dev.cankolay.trash.server.common.service.RateLimiter
import dev.cankolay.trash.server.common.util.Encryptor
import dev.cankolay.trash.server.module.application.repository.ApplicationRepository
import dev.cankolay.trash.server.module.auth.service.AuthService
import dev.cankolay.trash.server.module.connection.repository.ConnectionRepository
import dev.cankolay.trash.server.module.security.service.SecurityTokenService
import dev.cankolay.trash.server.module.session.repository.SessionRepository
import dev.cankolay.trash.server.module.storage.service.ObjectService
import dev.cankolay.trash.server.module.user.entity.Profile
import dev.cankolay.trash.server.module.user.entity.User
import dev.cankolay.trash.server.module.user.exception.UserExistsException
import dev.cankolay.trash.server.module.user.exception.UserNotFoundException
import dev.cankolay.trash.server.module.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val connectionRepository: ConnectionRepository,
    private val applicationRepository: ApplicationRepository,
    private val auth: AuthService,
    private val encryptor: Encryptor,
    private val securityTokenService: SecurityTokenService,
    private val rateLimiter: RateLimiter,
    private val profilePictureService: ProfilePictureService,
    private val objectService: ObjectService
) {
    @Transactional
    fun create(email: String, username: String, password: String, ip: String = "unknown"): User {
        if (!rateLimiter.check(key = "registration:$ip:${email.trim().lowercase(Locale.ROOT)}")) {
            throw RateLimitedException()
        }

        if (userRepository.existsByEmailOrUsername(email = email, username = username)) {
            throw UserExistsException()
        }

        return userRepository.save(
            User(
                email = email,
                username = username,
                password = encryptor.encrypt(password = password),
                profile = Profile()
            )
        )
    }

    @Transactional(readOnly = true)
    fun get(): User = auth.user()

    @Transactional(readOnly = true)
    fun getByUsername(username: String): User {
        val cleanUsername = username.removePrefix("@")
        return userRepository.findByUsername(username = cleanUsername)
            ?: userRepository.findById(username).orElseThrow { UserNotFoundException() }
    }

    @Transactional
    fun delete(securityToken: String) {
        auth.validateSession()
        securityTokenService.consume(jwt = securityToken)

        val user = auth.user()
        sessionRepository.deleteAll(sessionRepository.findAllByUserId(user.id))
        connectionRepository.deleteAll(connectionRepository.findAllByUserId(user.id))
        connectionRepository.deleteAll(connectionRepository.findAllByApplicationOwnerId(ownerId = user.id))

        sessionRepository.flush()
        connectionRepository.flush()

        applicationRepository.findAllByOwnerId(ownerId = user.id).forEach { objectService.delete(obj = it.icon) }
        applicationRepository.deleteAllByOwnerId(ownerId = user.id)

        profilePictureService.delete()

        userRepository.delete(user)
    }
}
