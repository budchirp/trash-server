package dev.cankolay.trash.server.module.session.service

import dev.cankolay.trash.server.common.exception.RateLimitedException
import dev.cankolay.trash.server.common.model.UserAgent
import dev.cankolay.trash.server.common.service.JwtService
import dev.cankolay.trash.server.common.service.RateLimiter
import dev.cankolay.trash.server.common.util.Encryptor
import dev.cankolay.trash.server.module.auth.service.AuthService
import dev.cankolay.trash.server.module.auth.service.TokenService
import dev.cankolay.trash.server.module.session.entity.Session
import dev.cankolay.trash.server.module.session.exception.InvalidCredentialsException
import dev.cankolay.trash.server.module.session.exception.SessionNotFoundException
import dev.cankolay.trash.server.module.session.repository.SessionRepository
import dev.cankolay.trash.server.module.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SessionService(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val tokenService: TokenService,
    private val jwtService: JwtService,
    private val auth: AuthService,
    private val encryptor: Encryptor,
    private val rateLimiter: RateLimiter
) {
    @Transactional
    fun create(userAgent: UserAgent, ip: String, email: String, password: String): String {
        val key = "login:$ip:${email.trim().lowercase(Locale.ROOT)}"
        if (!rateLimiter.check(key = key)) {
            throw RateLimitedException()
        }

        val user = userRepository.findByEmail(email = email)

        if (!encryptor.check(password = password, encrypted = user?.password)) {
            throw InvalidCredentialsException()
        }

        val authenticatedUser = user ?: throw InvalidCredentialsException()

        rateLimiter.reset(key = key)

        val token = tokenService.create()
        val session = sessionRepository.save(
            Session(
                token = token,

                ip = ip,

                device = userAgent.device,
                platform = userAgent.platform,

                os = userAgent.os,
                browser = userAgent.browser,

                user = authenticatedUser
            )
        )

        return jwtService.generateAccessToken(userId = authenticatedUser.id, token = session.token)
    }

    @Transactional(readOnly = true)
    fun getAll(): List<Session> = sessionRepository.findAllByUserId(userId = auth.id())

    @Transactional(readOnly = true)
    fun get(tokenId: String): Session =
        sessionRepository.findByTokenIdAndUserId(tokenId = tokenId, userId = auth.id())
            ?: throw SessionNotFoundException()

    @Transactional(readOnly = true)
    fun get(): Session = get(tokenId = auth.token().id)

    @Transactional
    fun delete(tokenId: String) {
        sessionRepository.delete(get(tokenId = tokenId))
    }

    @Transactional
    fun delete() = delete(tokenId = auth.token().id)
}
