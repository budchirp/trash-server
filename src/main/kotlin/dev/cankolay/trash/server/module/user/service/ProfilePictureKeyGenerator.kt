package dev.cankolay.trash.server.module.user.service

import org.springframework.stereotype.Component
import java.util.*

@Component
class ProfilePictureKeyGenerator {

    fun generate(userId: String, contentType: String): String {
        val extension = extensionFor(contentType)
        val id = UUID.randomUUID().toString()

        return "profiles/$userId/profile-picture/$id.$extension"
    }

    fun extensionFor(contentType: String): String =
        when (contentType.lowercase().trim()) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "bin"
        }
}
