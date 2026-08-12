package dev.cankolay.trash.server.module.user.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class DeleteUserRequestDto(
    @field:NotBlank
    @field:Size(max = 2048)
    val token: String
)
