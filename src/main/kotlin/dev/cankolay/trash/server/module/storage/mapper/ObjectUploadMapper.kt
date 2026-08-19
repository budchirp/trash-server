package dev.cankolay.trash.server.module.storage.mapper

import dev.cankolay.trash.server.module.storage.PresignedUpload
import dev.cankolay.trash.server.module.storage.dto.response.ObjectUploadResponseDto

fun PresignedUpload.toDto() = ObjectUploadResponseDto(
    url = url.toString(),
    key = key,
    expiresAt = expiresAt
)
