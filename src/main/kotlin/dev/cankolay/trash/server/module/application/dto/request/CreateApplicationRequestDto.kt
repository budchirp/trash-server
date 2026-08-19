package dev.cankolay.trash.server.module.application.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateApplicationRequestDto(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:NotBlank
    @field:Size(max = 500)
    val description: String
)
