package dev.cankolay.trash.server.module.storage.entity

import jakarta.persistence.*

@Entity
@Table(name = "objects")
class Object(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "s3_key", nullable = false, length = 1024)
    var key: String,

    @Column(nullable = false, length = 2048)
    var url: String,
)
