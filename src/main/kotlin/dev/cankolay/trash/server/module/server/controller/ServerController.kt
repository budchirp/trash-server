package dev.cankolay.trash.server.module.server.controller

import dev.cankolay.trash.server.common.model.ApiResponse
import dev.cankolay.trash.server.common.service.I18nService
import dev.cankolay.trash.server.module.server.dto.response.GetVersionResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/server")
class ServerController(
    private val i18nService: I18nService,
) {
    @GetMapping("/version")
    fun getVersion(): ResponseEntity<ApiResponse<GetVersionResponseDto>> = ResponseEntity.ok().body(
        ApiResponse(
            message = "success",
            code = i18nService.get("success"),
            data = GetVersionResponseDto(version = "v0.1.0")
        )
    )
}