package dev.cankolay.trash.server.module.user.controller

import dev.cankolay.trash.server.common.model.ApiResponse
import dev.cankolay.trash.server.common.security.annotation.Authenticate
import dev.cankolay.trash.server.common.service.I18nService
import dev.cankolay.trash.server.common.util.Controller
import dev.cankolay.trash.server.module.user.dto.request.UpdateProfileRequestDto
import dev.cankolay.trash.server.module.user.service.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user/profile")
class ProfileController(
    private val controller: Controller,
    private val i18nService: I18nService,
    private val profileService: ProfileService
) {
    @Authenticate
    @PatchMapping
    fun update(@RequestBody body: UpdateProfileRequestDto): ResponseEntity<ApiResponse<Nothing>> =
        controller {
            profileService.update(name = body.name)

            ResponseEntity.ok().body(
                ApiResponse(
                    message = i18nService.get("success"),
                    code = "success"
                )
            )
        }
}