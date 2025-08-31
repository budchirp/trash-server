package com.cankolay.trash.server.module.user.controller

import com.cankolay.trash.server.common.model.ApiResponse
import com.cankolay.trash.server.common.security.annotation.Authenticate
import com.cankolay.trash.server.common.service.I18nService
import com.cankolay.trash.server.common.util.SafeController
import com.cankolay.trash.server.module.user.dto.request.CreateTokenRequestDto
import com.cankolay.trash.server.module.user.dto.request.CreateUserRequestDto
import com.cankolay.trash.server.module.user.dto.response.CreateTokenResponseDto
import com.cankolay.trash.server.module.user.dto.response.GetProfileResponseDto
import com.cankolay.trash.server.module.user.entity.toDto
import com.cankolay.trash.server.module.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val safeController: SafeController,
    private val i18nService: I18nService,
    private val userService: UserService,
) {
    @Authenticate
    @GetMapping
    fun get(): ResponseEntity<ApiResponse<GetProfileResponseDto>> =
        safeController {
            ResponseEntity.ok().body(
                ApiResponse(
                    message = i18nService.get("success"),
                    code = "success",
                    data = userService.get().toDto()
                )
            )
        }


    @PostMapping
    fun create(@RequestBody body: CreateUserRequestDto): ResponseEntity<ApiResponse<Nothing>> =
        safeController {
            userService.create(email = body.email, username = body.username, password = body.password)

            ResponseEntity.ok().body(
                ApiResponse(
                    message = i18nService.get("success"),
                    code = "success"
                )
            )
        }

    
    @Authenticate
    @DeleteMapping
    fun delete(@RequestParam("token") token: String): ResponseEntity<ApiResponse<Nothing>> =
        safeController {
            userService.delete(token = token)

            ResponseEntity.ok().body(
                ApiResponse(
                    message = i18nService.get("success"),
                    code = "success"
                )
            )
        }

    @Authenticate
    @PostMapping("/security/token")
    fun createSecurityToken(@RequestBody body: CreateTokenRequestDto): ResponseEntity<ApiResponse<CreateTokenResponseDto>> =
        safeController {
            ResponseEntity.ok().body(
                ApiResponse(
                    message = i18nService.get("success"),
                    code = "success",
                    data = CreateTokenResponseDto(
                        token = userService.createToken(password = body.password)
                    )
                )
            )
        }
}