package dev.cankolay.trash.server.module.user.dto

data class UserProfileDto(
    val username: String,

    val profile: ProfileDto? = null
)
