package com.cankolay.trash.server.common.util

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.stereotype.Component

@Component
class Encryptor {
    private val encoder = Argon2PasswordEncoder(
        16,
        32,
        1,
        65536,
        4
    )

    fun encrypt(password: String): String = encoder.encode(password)

    fun check(password: String, encrypted: String): Boolean = encoder.matches(password, encrypted)
}