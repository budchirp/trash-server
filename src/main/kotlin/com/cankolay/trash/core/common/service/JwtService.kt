package com.cankolay.trash.core.common.service

import com.cankolay.trash.core.module.session.model.JWTPayload
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${app.jwt.secret}")
    private val secret: String
) {
    private fun getKey(): SecretKey? {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
    }

    fun extract(token: String?): String? =
        if (token != null && token.startsWith(prefix = "Bearer ")) token.removePrefix(prefix = "Bearer ") else null

    fun generate(id: Long, token: String): String {
        val key = getKey()

        return Jwts.builder()
            .subject("user")
            .claim("id", id.toString()).claim("token", token)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 15))
            .signWith(key)
            .compact()
    }

    fun generate(id: Long): String {
        val key = getKey()

        return Jwts.builder()
            .subject("verification").claim("id", id.toString())
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .signWith(key)
            .compact()
    }

    fun verify(jwt: String?): Boolean {
        if (jwt == null) return false

        return try {
            val key = getKey()
            Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt)

            true
        } catch (_: Exception) {
            false
        }
    }

    fun get(jwt: String): JWTPayload {
        val key = getKey()
        val payload = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).payload

        return JWTPayload(
            id = payload["id"].toString().toLong(),
            token = payload["token"].toString()
        )
    }

    fun getId(jwt: String): Long {
        val key = getKey()
        val payload = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).payload

        return payload["id"].toString().toLong()
    }
}