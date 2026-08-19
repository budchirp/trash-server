package dev.cankolay.trash.server.module.user.dto

data class ProfileDto(
    val name: String?,
    val picture: String?,
    val gender: String?,
    val `public`: Boolean,
    val dev: Boolean
)
