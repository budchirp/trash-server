package dev.cankolay.trash.server.module.user.mapper

import dev.cankolay.trash.server.module.user.dto.ProfileDto
import dev.cankolay.trash.server.module.user.entity.Profile

fun Profile.toDto() = ProfileDto(
    name = name,
    picture = picture?.url,
    gender = gender?.value,
    `public` = `public`,
    dev = dev
)
