package dev.cankolay.trash.server.module.auth.service

import dev.cankolay.trash.server.common.model.JwtPurpose
import dev.cankolay.trash.server.common.service.JwtService
import dev.cankolay.trash.server.module.auth.entity.Token
import dev.cankolay.trash.server.module.auth.entity.TokenType
import dev.cankolay.trash.server.module.auth.model.AuthPrincipal
import dev.cankolay.trash.server.module.connection.repository.ConnectionRepository
import dev.cankolay.trash.server.module.security.PermissionKeys
import dev.cankolay.trash.server.module.security.exception.InsufficientPermissionsException
import dev.cankolay.trash.server.module.session.exception.UnauthorizedException
import dev.cankolay.trash.server.module.session.repository.SessionRepository
import dev.cankolay.trash.server.module.user.entity.User
import dev.cankolay.trash.server.module.user.exception.UserNotFoundException
import dev.cankolay.trash.server.module.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AuthService(
    private val jwtService: JwtService,
    private val tokenService: TokenService,
    private val sessionRepository: SessionRepository,
    private val connectionRepository: ConnectionRepository,
    private val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun authenticate(jwt: String): AuthPrincipal? {
        val payload = jwtService.parse(jwt = jwt) ?: return null
        if (payload.purpose != JwtPurpose.ACCESS) return null

        val tokenId = payload.id ?: return null
        val type = payload.type ?: return null
        val token = runCatching { tokenService.get(id = tokenId) }.getOrNull() ?: return null
        if (token.type != type || token.expiresAt.isBefore(Instant.now())) return null

        if (!isTokenBelogsToUser(tokenId = tokenId, userId = payload.user, type = type)) return null

        return AuthPrincipal(
            user = payload.user,
            token = tokenId,
            type = type,
            permissions = expandPermissions(permissions = token.permissions)
        )
    }

    fun principal(): AuthPrincipal =
        SecurityContextHolder.getContext().authentication?.principal as? AuthPrincipal
            ?: throw UnauthorizedException()

    fun id(): String = principal().user

    fun permissions(): Set<String> = principal().permissions

    @Transactional(readOnly = true)
    fun user(): User = userRepository.findById(id()).orElseThrow { UserNotFoundException() }

    @Transactional(readOnly = true)
    fun token(): Token = tokenService.get(id = principal().token)

    fun validateSession() {
        if (principal().type != TokenType.SESSION) {
            throw InsufficientPermissionsException()
        }
    }

    private fun isTokenBelogsToUser(tokenId: String, userId: String, type: TokenType): Boolean =
        when (type) {
            TokenType.SESSION -> sessionRepository.findByTokenIdAndUserId(
                tokenId = tokenId,
                userId = userId
            ) != null

            TokenType.CONNECTION -> connectionRepository.findByTokenIdAndUserId(
                tokenId = tokenId,
                userId = userId
            ) != null

            TokenType.SECURITY -> false
        }

    private fun expandPermissions(permissions: Set<String>): Set<String> =
        if (PermissionKeys.WILDCARD in permissions) PermissionKeys.ALL else permissions
}
