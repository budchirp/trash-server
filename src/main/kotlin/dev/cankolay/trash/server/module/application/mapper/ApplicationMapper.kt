package dev.cankolay.trash.server.module.application.mapper

import dev.cankolay.trash.server.module.application.dto.ApplicationDto
import dev.cankolay.trash.server.module.application.entity.Application

fun Application.toDto() = ApplicationDto(
    id = id,
    name = name,
    description = description,
    icon = icon?.url,
    createdAt = createdAt
)
