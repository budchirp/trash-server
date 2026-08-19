package dev.cankolay.trash.server.module.application.controller

import dev.cankolay.trash.server.common.model.ApiResponse
import dev.cankolay.trash.server.common.web.ApiResponseFactory
import dev.cankolay.trash.server.module.application.dto.ApplicationDto
import dev.cankolay.trash.server.module.application.dto.request.CreateApplicationRequestDto
import dev.cankolay.trash.server.module.application.mapper.toDto
import dev.cankolay.trash.server.module.application.service.ApplicationIconService
import dev.cankolay.trash.server.module.application.service.ApplicationService
import dev.cankolay.trash.server.module.storage.dto.request.ObjectUploadRequestDto
import dev.cankolay.trash.server.module.storage.dto.response.ObjectUploadResponseDto
import dev.cankolay.trash.server.module.storage.mapper.toDto
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/application")
class ApplicationController(
    private val responses: ApiResponseFactory,
    private val applicationService: ApplicationService,
    private val applicationIconService: ApplicationIconService
) {
    @PostMapping
    fun create(@Valid @RequestBody body: CreateApplicationRequestDto): ResponseEntity<ApiResponse<ApplicationDto>> =
        responses.created(
            data = applicationService.create(
                name = body.name,
                description = body.description
            ).toDto()
        )

    @PostMapping("/{id}/icon")
    fun uploadIcon(
        @PathVariable id: String,
        @Valid @RequestBody body: ObjectUploadRequestDto
    ): ResponseEntity<ApiResponse<ObjectUploadResponseDto>> =
        responses.ok(
            data = applicationIconService.create(
                applicationId = id,
                contentType = body.contentType,
                contentLength = body.contentLength
            ).toDto()
        )

    @DeleteMapping("/{id}/icon")
    fun deleteIcon(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        applicationIconService.delete(applicationId = id)

        return responses.ok()
    }

    @GetMapping("/all")
    fun getAll(): ResponseEntity<ApiResponse<List<ApplicationDto>>> =
        responses.ok(data = applicationService.getAll().map { it.toDto() })

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): ResponseEntity<ApiResponse<ApplicationDto>> =
        responses.ok(data = applicationService.get(id = id).toDto())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        applicationService.delete(id = id)

        return responses.ok()
    }
}
