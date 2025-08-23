package com.cankolay.trash.core.module.user.entity

import com.cankolay.trash.core.module.user.dto.ProfileDto
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
@Table(name = "profiles")
data class Profile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String,
    val picture: String? = null,

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    val createdAt: Date = Date(),

    @UpdateTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    val updatedAt: Date = Date(),
)

fun Profile.toDto() = ProfileDto(
    name = this.name
)