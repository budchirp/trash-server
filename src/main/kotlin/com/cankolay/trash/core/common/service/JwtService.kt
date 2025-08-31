package com.cankolay.trash.core.common.service

import com.cankolay.trash.core.module.session.model.JWTPayload
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    @Value("\${app.jwt.secret}")
    private val secret: String
) {
    private fun secretKey() = Keys.hmacShaKeyFor(secret.toByteArray())

    fun extract(token: String?): String? =
        if (token != null && token.startsWith(prefix = "Bearer ")) token.removePrefix(prefix = "Bearer ") else null

    fun generate(id: String, token: String? = null, duration: Long = 1000L * 60 * 60 * 24 * 30): String {
        val now = System.currentTimeMillis()

        return Jwts.builder()
            .subject("user")
            .claim("id", id)
            .claim("token", token)
            .expiration(Date(now + duration))
            .issuedAt(Date(now))
            .signWith(secretKey())
            .compact()
    }


    fun verify(jwt: String): Boolean = try {
        Jwts.parser().verifyWith(secretKey()).build().parseSignedClaims(jwt)

        true
    } catch (_: Exception) {
        false
    }

    fun payload(jwt: String): JWTPayload {
        val payload = Jwts.parser().verifyWith(secretKey()).build().parseSignedClaims(jwt).payload

        return JWTPayload(
            id = payload["id"].toString(),
            token = payload["token"].toString()
        )
    }

    fun id(jwt: String): String {
        val payload = Jwts.parser().verifyWith(secretKey()).build().parseSignedClaims(jwt).payload

        return payload["id"].toString()
    }
}