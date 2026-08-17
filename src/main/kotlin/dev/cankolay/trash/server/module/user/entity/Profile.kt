package dev.cankolay.trash.server.module.user.entity

import jakarta.persistence.*

@Entity
@Table(name = "profiles")
class Profile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(length = 100)
    var name: String? = null,

    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "picture_id")
    var picture: Picture? = null,

    @Enumerated(EnumType.STRING)
    var gender: ProfileGender? = null,

    @Column(nullable = false)
    var `public`: Boolean = false,
)

enum class ProfileGender(val value: String) {
    MALE(value = "male"),
    FEMALE(value = "female");

    companion object {
        fun fromValue(value: String): ProfileGender? =
            entries.firstOrNull { it.value.equals(value, ignoreCase = true) }
    }
}
