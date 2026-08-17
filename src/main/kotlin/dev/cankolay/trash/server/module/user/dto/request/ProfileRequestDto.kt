package dev.cankolay.trash.server.module.user.dto.request

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ProfileRequestDto(
    @field:Size(max = 100)
    val name: String?,

    @field:Pattern(regexp = "male|female", flags = [Pattern.Flag.CASE_INSENSITIVE])
    val gender: String? = null,

    val `public`: Boolean? = null
)
