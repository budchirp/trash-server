package dev.cankolay.trash.server.module.storage.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class ObjectUploadRequestDto(
    @field:NotBlank
    val contentType: String,

    @field:Positive
    val contentLength: Long? = null
)
