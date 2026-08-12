package dev.cankolay.trash.server.module.security.service

import dev.cankolay.trash.server.common.exception.RateLimitedException
import dev.cankolay.trash.server.common.model.JwtPurpose
import dev.cankolay.trash.server.common.service.JwtService
import dev.cankolay.trash.server.common.service.RateLimiter
import dev.cankolay.trash.server.common.util.Encryptor
import dev.cankolay.trash.server.module.auth.entity.Token
import dev.cankolay.trash.server.module.auth.entity.TokenType
import dev.cankolay.trash.server.module.auth.service.AuthService
import dev.cankolay.trash.server.module.auth.service.TokenService
import dev.cankolay.trash.server.module.security.exception.InvalidSecurityTokenException
import dev.cankolay.trash.server.module.session.exception.InvalidPasswordException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant

@Service
class SecurityTokenService(
    private val jwtService: JwtService,
    private val encryptor: Encryptor,
    private val auth: AuthService,
    private val tokenService: TokenService,
    private val rateLimiter: RateLimiter
) {

    @Transactional
    fun create(password: String): String {
        auth.validateSession()

        val user = auth.user()

        val key = "security-token:${user.id}"
        if (!rateLimiter.check(key)) {
            throw RateLimitedException()
        }

        if (!encryptor.check(password, user.password)) {
            throw InvalidPasswordException()
        }

        val expiresAt = Instant.now().plus(SECURITY_TOKEN_DURATION)
        val token = tokenService.createSecurity(user.id, expiresAt)

        rateLimiter.reset(key)

        return jwtService.generateSecurityToken(
            userId = user.id,
            token = token,
            duration = SECURITY_TOKEN_DURATION
        )
    }

    @Transactional(readOnly = true)
    fun verify(jwt: String) {
        auth.validateSession()

        validate(jwt)
    }

    @Transactional
    fun consume(jwt: String) {
        auth.validateSession()

        val token = validate(jwt)
        if (!tokenService.consumeSecurity(token.id, Instant.now())) {
            throw InvalidSecurityTokenException()
        }
    }

    private fun validate(jwt: String): Token {
        val userId = auth.id()
        val payload = jwtService.parse(jwt)
            ?.takeIf {
                it.purpose == JwtPurpose.SECURITY &&
                        it.user == userId &&
                        it.type == TokenType.SECURITY
            }

        val tokenId = payload?.id
            ?.takeIf(String::isNotBlank)
            ?: throw InvalidSecurityTokenException()

        val token = runCatching {
            tokenService.get(tokenId)
        }.getOrNull() ?: throw InvalidSecurityTokenException()

        val valid = token.type == TokenType.SECURITY &&
                token.ownerId == userId &&
                token.expiresAt.isAfter(Instant.now())

        if (!valid) {
            throw InvalidSecurityTokenException()
        }

        return token
    }

    private companion object {
        val SECURITY_TOKEN_DURATION: Duration = Duration.ofMinutes(15)
    }
}