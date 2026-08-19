package dev.cankolay.trash.server.module.storage.service

import org.springframework.stereotype.Component
import java.util.*

@Component
class ObjectKeyGenerator {

    fun generate(prefix: String, contentType: String): String {
        val extension = extensionFor(contentType)
        val id = UUID.randomUUID().toString()

        return "$prefix/$id.$extension"
    }

    fun extensionFor(contentType: String): String =
        when (contentType.lowercase().trim()) {
            "image/jpeg", "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "bin"
        }
}
