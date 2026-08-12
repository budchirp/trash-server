package dev.cankolay.trash.server.module.auth.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

enum class TokenType {
    SESSION,
    CONNECTION,
    SECURITY
}

@Entity
@Table(name = "tokens")
class Token(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    val type: TokenType = TokenType.SESSION,

    @Column(name = "owner_id", length = 36)
    val ownerId: String? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "token_permissions",
        joinColumns = [JoinColumn(name = "token_id")]
    )
    @Column(name = "permission", nullable = false)
    val permissions: MutableSet<String> = mutableSetOf(),

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant = defaultExpiresAt(),

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: Instant? = null,
) {
    fun grant(permissionKeys: Set<String>) {
        permissions.clear()
        permissions.addAll(elements = permissionKeys)
        expiresAt = defaultExpiresAt()
    }

    companion object {
        fun defaultExpiresAt(): Instant = Instant.now().plus(30, ChronoUnit.DAYS)
    }
}
