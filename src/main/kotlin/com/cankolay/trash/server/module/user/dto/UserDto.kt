package com.cankolay.trash.server.module.user.dto

data class UserDto(
    val id: String,
    val email: String,
    val username: String,
    val profile: ProfileDto
)