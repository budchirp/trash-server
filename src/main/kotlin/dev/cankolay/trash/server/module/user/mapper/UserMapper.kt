package dev.cankolay.trash.server.module.user.mapper

import dev.cankolay.trash.server.module.user.dto.UserDto
import dev.cankolay.trash.server.module.user.dto.UserProfileDto
import dev.cankolay.trash.server.module.user.entity.User

fun User.toDto() = UserDto(
    id = id,
    email = email,
    username = username,
    profile = profile.toDto()
)

fun User.toProfileDto() = UserProfileDto(
    username = username,
    profile = if (profile.public) profile.toDto() else null
)

