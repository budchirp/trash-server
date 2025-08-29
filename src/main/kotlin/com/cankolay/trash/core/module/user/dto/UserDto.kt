package com.cankolay.trash.core.module.user.dto

data class UserDto(
    val id: String,
    val email: String,
    val username: String,
    val profile: ProfileDto
)