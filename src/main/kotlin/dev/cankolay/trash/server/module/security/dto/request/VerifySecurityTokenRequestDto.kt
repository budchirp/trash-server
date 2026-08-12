package dev.cankolay.trash.server.module.security.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class VerifySecurityTokenRequestDto(
    @field:NotBlank
    @field:Size(max = 2048)
    val token: String,
)
