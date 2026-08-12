package dev.cankolay.trash.server.module.session.controller

import dev.cankolay.trash.server.common.model.ApiResponse
import dev.cankolay.trash.server.common.service.RequestInfoService
import dev.cankolay.trash.server.common.util.UserAgentParser
import dev.cankolay.trash.server.common.web.ApiResponseFactory
import dev.cankolay.trash.server.module.session.dto.SessionDto
import dev.cankolay.trash.server.module.session.dto.request.CreateSessionRequestDto
import dev.cankolay.trash.server.module.session.dto.response.CreateSessionResponseDto
import dev.cankolay.trash.server.module.session.mapper.toDto
import dev.cankolay.trash.server.module.session.service.SessionService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/session")
class SessionController(
    private val responses: ApiResponseFactory,
    private val sessionService: SessionService,
    private val requestInfoService: RequestInfoService,
    private val userAgentParser: UserAgentParser
) {
    @PostMapping
    fun create(
        request: HttpServletRequest,
        @Valid @RequestBody body: CreateSessionRequestDto
    ): ResponseEntity<ApiResponse<CreateSessionResponseDto>> {
        val userAgent = requestInfoService.getUserAgent(request = request)
        val ip = requestInfoService.getClientIp(request = request)

        return responses.ok(
            data = CreateSessionResponseDto(
                token = sessionService.create(
                    userAgent = userAgentParser(userAgent = userAgent),
                    ip = ip,
                    email = body.email,
                    password = body.password
                )
            )
        )
    }

    @GetMapping("/all")
    fun getAll(): ResponseEntity<ApiResponse<List<SessionDto>>> =
        responses.ok(data = sessionService.getAll().map { it.toDto() })

    @GetMapping("/{token}")
    fun get(@PathVariable token: String): ResponseEntity<ApiResponse<SessionDto>> =
        responses.ok(data = sessionService.get(tokenId = token).toDto())

    @GetMapping
    fun get(): ResponseEntity<ApiResponse<SessionDto>> =
        responses.ok(data = sessionService.get().toDto())

    @DeleteMapping
    fun delete(): ResponseEntity<ApiResponse<Nothing>> {
        sessionService.delete()
        return responses.ok()
    }

    @DeleteMapping("/{token}")
    fun delete(@PathVariable token: String): ResponseEntity<ApiResponse<Nothing>> {
        sessionService.delete(tokenId = token)
        return responses.ok()
    }
}
