package dev.cankolay.trash.server.module.user.controller

import dev.cankolay.trash.server.common.model.ApiResponse
import dev.cankolay.trash.server.common.web.ApiResponseFactory
import dev.cankolay.trash.server.module.user.dto.ProfileDto
import dev.cankolay.trash.server.module.user.dto.request.ProfilePictureUploadRequestDto
import dev.cankolay.trash.server.module.user.dto.request.ProfileRequestDto
import dev.cankolay.trash.server.module.user.dto.response.ProfilePictureUploadResponseDto
import dev.cankolay.trash.server.module.user.mapper.toDto
import dev.cankolay.trash.server.module.user.service.ProfilePictureService
import dev.cankolay.trash.server.module.user.service.ProfileService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/profile")
class ProfileController(
    private val responses: ApiResponseFactory,
    private val profileService: ProfileService,
    private val profilePictureService: ProfilePictureService
) {
    @PatchMapping
    fun update(@Valid @RequestBody body: ProfileRequestDto): ResponseEntity<ApiResponse<ProfileDto>> {
        val profile = profileService.update(
            name = body.name,
            gender = body.gender,
            `public` = body.`public`
        )

        return responses.ok(data = profile.toDto())
    }

    @PostMapping("/picture")
    fun upload(
        @Valid @RequestBody body: ProfilePictureUploadRequestDto
    ): ResponseEntity<ApiResponse<ProfilePictureUploadResponseDto>> {
        val upload = profilePictureService.create(
            contentType = body.contentType,
            contentLength = body.contentLength
        )

        return responses.ok(
            data = ProfilePictureUploadResponseDto(
                url = upload.url.toString(),
                key = upload.key,
                expiresAt = upload.expiresAt
            )
        )
    }

    @DeleteMapping("/picture")
    fun delete(): ResponseEntity<ApiResponse<Nothing>> {
        profilePictureService.delete()
        return responses.ok()
    }
}
