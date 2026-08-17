package dev.cankolay.trash.server.module.user.entity

import jakarta.persistence.*

@Entity
@Table(name = "pictures")
class Picture(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 2048)
    var url: String,

    @Column(name = "s3_key", nullable = false, length = 1024)
    var key: String,
)
