package dev.cankolay.trash.server.module.user.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class ProfilePictureUploadRequestDto(
    @field:NotBlank
    val contentType: String,

    @field:Positive
    val contentLength: Long? = null
)
