package com.cankolay.trash.server.module.user.entity

import com.cankolay.trash.server.module.session.entity.Session
import com.cankolay.trash.server.module.user.dto.UserDto
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: String = UUID.randomUUID().toString(),

    val email: String,
    var username: String,

    val password: String,

    val verified: Boolean = false,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    var profile: Profile,

    @OneToMany(mappedBy = "user")
    var sessions: MutableSet<Session> = mutableSetOf(),

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    val createdAt: Date = Date(),

    @UpdateTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    val updatedAt: Date = Date(),
)

fun User.toDto() = UserDto(
    id = this.id,
    email = this.email,
    username = this.username,
    profile = this.profile.toDto()
)