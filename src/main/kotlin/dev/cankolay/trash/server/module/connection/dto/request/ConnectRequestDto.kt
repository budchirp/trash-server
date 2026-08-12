package dev.cankolay.trash.server.module.connection.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class ConnectRequestDto(
    @param:JsonProperty("applicationId")
    @field:NotBlank
    @field:Size(max = 36)
    val applicationId: String,

    @field:NotEmpty
    @field:Size(max = 16)
    val permissions: Set<String>
)
