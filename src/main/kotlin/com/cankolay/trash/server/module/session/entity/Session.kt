package com.cankolay.trash.server.module.session.entity

import com.cankolay.trash.server.common.service.UserAgentPlatform
import com.cankolay.trash.server.module.session.dto.SessionDto
import com.cankolay.trash.server.module.user.entity.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*


@Entity
@Table(name = "sessions")
data class Session(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val token: String,

    val ip: String,

    val device: String,
    @Enumerated(value = EnumType.STRING)
    val platform: UserAgentPlatform,

    val os: String,
    val browser: String,

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "expires_at")
    val expiresAt: Date = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000),

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    val user: User,

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    val createdAt: Date = Date(),

    @UpdateTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    val updatedAt: Date = Date(),
)

fun Session.toDto() = SessionDto(
    token = this.token,
    ip = this.ip,
    device = this.device,
    platform = this.platform.name,
    os = this.os,
    browser = this.browser,
    expiresAt = this.expiresAt.toString()
)