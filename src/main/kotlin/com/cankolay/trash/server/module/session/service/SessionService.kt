package com.cankolay.trash.server.module.session.service

import com.cankolay.trash.server.common.service.AuthService
import com.cankolay.trash.server.common.service.JwtService
import com.cankolay.trash.server.common.service.UserAgent
import com.cankolay.trash.server.common.util.Encryptor
import com.cankolay.trash.server.module.session.entity.Session
import com.cankolay.trash.server.module.session.exception.InvalidPasswordException
import com.cankolay.trash.server.module.session.exception.SessionNotFoundException
import com.cankolay.trash.server.module.session.exception.UnauthorizedException
import com.cankolay.trash.server.module.session.exception.UserNotFoundException
import com.cankolay.trash.server.module.session.repository.SessionRepository
import com.cankolay.trash.server.module.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID.randomUUID

@Service
class SessionService(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val jwtService: JwtService,
    private val authService: AuthService,
    private val encryptor: Encryptor
) {
    @Transactional
    @Throws(InvalidPasswordException::class, UserNotFoundException::class)
    fun create(userAgent: UserAgent, ip: String, username: String, password: String): String {
        val user = userRepository.findByUsername(username) ?: throw UserNotFoundException()

        if (!encryptor.check(password = password, encrypted = user.password)) {
            throw InvalidPasswordException()
        }

        val session = sessionRepository.save(
            Session(
                token = randomUUID().toString(),

                ip = ip,

                device = userAgent.device,
                platform = userAgent.platform,

                os = userAgent.os,
                browser = userAgent.browser,

                user = user
            )
        )

        return jwtService.generate(id = user.id, token = session.token)
    }

    @Transactional
    @Throws(UserNotFoundException::class, UnauthorizedException::class)
    fun delete() {
        val user = authService.user()
        val token = authService.token()

        sessionRepository.deleteByTokenAndUserId(token = token, user = user.id)
    }

    @Transactional
    @Throws(UserNotFoundException::class, UnauthorizedException::class)
    fun delete(token: String) {
        val user = authService.user()

        if (!exists(user = user.id, token = token)) {
            throw UnauthorizedException()
        }

        sessionRepository.deleteByTokenAndUserId(token = token, user = user.id)
    }

    @Throws(UserNotFoundException::class, UnauthorizedException::class)
    fun getAll(): List<Session> {
        val user = authService.user()

        return sessionRepository.findAllByUserId(user = user.id)
    }

    @Throws(SessionNotFoundException::class, UserNotFoundException::class, UnauthorizedException::class)
    fun get(token: String): Session {
        val user = authService.user()

        return sessionRepository.findByTokenAndUserId(token = token, user = user.id) ?: throw SessionNotFoundException()
    }

    fun exists(user: String, token: String): Boolean =
        sessionRepository.existsByTokenAndUserId(token = token, user = user)
}