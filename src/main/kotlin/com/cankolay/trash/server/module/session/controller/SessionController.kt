package com.cankolay.trash.server.module.session.controller

import com.cankolay.trash.server.common.model.ApiResponse
import com.cankolay.trash.server.common.security.annotation.Authenticate
import com.cankolay.trash.server.common.service.I18nService
import com.cankolay.trash.server.common.service.RequestInfoService
import com.cankolay.trash.server.common.service.UserAgentParser
import com.cankolay.trash.server.common.util.SafeController
import com.cankolay.trash.server.module.session.dto.request.CreateSessionRequestDto
import com.cankolay.trash.server.module.session.dto.response.CreateSessionResponseDto
import com.cankolay.trash.server.module.session.dto.response.GetAllSessionsResponse
import com.cankolay.trash.server.module.session.dto.response.GetSessionResponse
import com.cankolay.trash.server.module.session.entity.toDto
import com.cankolay.trash.server.module.session.service.SessionService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/session")
class SessionController(
    private val safeController: SafeController,
    private val i18nService: I18nService,
    private val sessionService: SessionService,
    private val requestInfoService: RequestInfoService,
    private val userAgentParser: UserAgentParser
) {
    @PostMapping
    fun create(
        request: HttpServletRequest,
        @RequestBody body: CreateSessionRequestDto
    ): ResponseEntity<ApiResponse<CreateSessionResponseDto>> =
        safeController {
            val userAgent = requestInfoService.getUserAgent(request)
            val ip = requestInfoService.getClientIp(request)

            ResponseEntity.ok().body(
                ApiResponse(
                    message = i18nService.get("success"),
                    code = "success",
                    data = CreateSessionResponseDto(
                        token = sessionService.create(
                            userAgent = userAgentParser(userAgent),
                            ip = ip,
                            username = body.username,
                            password = body.password
                        )
                    )
                )
            )
        }

    @Authenticate
    @DeleteMapping
    fun delete(): ResponseEntity<ApiResponse<Nothing>> =
        safeController {
            sessionService.delete()

            ResponseEntity.ok().body(
                ApiResponse(
                    message = i18nService.get("success"),
                    code = "success"
                )
            )
        }

    @Authenticate
    @DeleteMapping("/{token}")
    fun delete(@PathVariable token: String): ResponseEntity<ApiResponse<Nothing>> =
        safeController {
            sessionService.delete(token = token)

            ResponseEntity.ok().body(
                ApiResponse(
                    message = i18nService.get("success"),
                    code = "success"
                )
            )
        }

    @Authenticate
    @GetMapping
    fun getAll(): ResponseEntity<ApiResponse<GetAllSessionsResponse>> =
        safeController {
            ResponseEntity.ok().body(
                ApiResponse(
                    message = i18nService.get("success"),
                    code = "success",
                    data = sessionService.getAll().map { session -> session.toDto() }
                )
            )
        }

    @Authenticate
    @GetMapping("/{token}")
    fun get(@PathVariable token: String): ResponseEntity<ApiResponse<GetSessionResponse>> =
        safeController {
            ResponseEntity.ok().body(
                ApiResponse(
                    message = i18nService.get("success"),
                    code = "success",
                    data = sessionService.get(token = token).toDto()
                )
            )
        }

    @Authenticate
    @GetMapping("/verify")
    fun verify(): ResponseEntity<ApiResponse<Nothing>> =
        safeController {
            ResponseEntity.ok().body(
                ApiResponse(
                    message = i18nService.get("success"),
                    code = "success"
                )
            )
        }
}