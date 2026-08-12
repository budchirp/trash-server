package dev.cankolay.trash.server.common.service

import dev.cankolay.trash.server.common.model.JWTPayload
import dev.cankolay.trash.server.common.model.JwtPurpose
import dev.cankolay.trash.server.module.auth.entity.Token
import dev.cankolay.trash.server.module.auth.entity.TokenType
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class JwtService(
    @param:Value("\${app.jwt.secret}")
    private val secret: String,
    @param:Value("\${app.jwt.issuer}")
    private val issuer: String,
    @param:Value("\${app.jwt.audience}")
    private val audience: String
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun extract(jwt: String?): String? {
        val parts = jwt?.trim()?.split(Regex("\\s+"), limit = 2) ?: return null
        return parts
            .takeIf { it.size == 2 && it[0].equals("Bearer", ignoreCase = true) }
            ?.get(index = 1)
            ?.takeIf(String::isNotBlank)
    }

    fun generateAccessToken(
        userId: String,
        token: Token,
        duration: Duration = Duration.ofDays(30)
    ): String = generate(
        userId = userId,
        token = token,
        purpose = JwtPurpose.ACCESS,
        duration = duration
    )

    fun generateSecurityToken(
        userId: String,
        token: Token,
        duration: Duration = Duration.ofMinutes(15)
    ): String = generate(
        userId = userId,
        token = token,
        purpose = JwtPurpose.SECURITY,
        duration = duration
    )

    fun parse(jwt: String): JWTPayload? = runCatching {
        val claims = Jwts.parser()
            .verifyWith(key)
            .requireIssuer(issuer)
            .requireAudience(audience)
            .build()
            .parseSignedClaims(jwt)
            .payload

        JWTPayload(
            user = claims[USER_CLAIM]?.toString().orEmpty(),
            id = claims[ID_CLAIM]?.toString(),
            type = claims[TYPE_CLAIM]?.toString()?.let(TokenType::valueOf),
            purpose = JwtPurpose.valueOf(claims[PURPOSE_CLAIM]?.toString().orEmpty())
        )
    }.getOrNull()?.takeIf { it.user.isNotBlank() }

    private fun generate(
        userId: String,
        token: Token?,
        purpose: JwtPurpose,
        duration: Duration,
        issuedAt: Instant = Instant.now()
    ): String {
        val builder = Jwts.builder()
            .claim(USER_CLAIM, userId)
            .claim(PURPOSE_CLAIM, purpose.name)
            .issuer(issuer)
            .audience()
            .add(audience)
            .and()

        if (token != null) {
            builder
                .claim(ID_CLAIM, token.id)
                .claim(TYPE_CLAIM, token.type.name)
        }

        return builder
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(issuedAt.plus(duration)))
            .signWith(key)
            .compact()
    }

    companion object {
        private const val ID_CLAIM = "id"
        private const val USER_CLAIM = "user"
        private const val TYPE_CLAIM = "type"
        private const val PURPOSE_CLAIM = "purpose"
    }
}
