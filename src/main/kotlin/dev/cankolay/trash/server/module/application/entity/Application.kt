package dev.cankolay.trash.server.module.application.entity

import dev.cankolay.trash.server.module.storage.entity.Object
import dev.cankolay.trash.server.module.user.entity.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "applications")
class Application(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false, length = 500)
    val description: String,

    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "icon_id")
    var icon: Object? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val owner: User,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: Instant? = null,
)
