package dev.cankolay.trash.server.module.user.controller

import dev.cankolay.trash.server.common.model.ApiResponse
import dev.cankolay.trash.server.common.security.annotation.Authenticate
import dev.cankolay.trash.server.common.service.I18nService
import dev.cankolay.trash.server.common.util.Controller
import dev.cankolay.trash.server.module.user.dto.request.CreateTokenRequestDto
import dev.cankolay.trash.server.module.user.dto.request.CreateUserRequestDto
import dev.cankolay.trash.server.module.user.dto.response.CreateTokenResponseDto
import dev.cankolay.trash.server.module.user.dto.response.GetProfileResponseDto
import dev.cankolay.trash.server.module.user.entity.toDto
import dev.cankolay.trash.server.module.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val controller: Controller,
    private val i18nService: I18nService,
    private val userService: UserService,
) {
    @Authenticate
    @GetMapping
    fun get(): ResponseEntity<ApiResponse<GetProfileResponseDto>> =
        controller {
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
        controller {
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
    fun delete(@RequestParam(value = "token") token: String): ResponseEntity<ApiResponse<Nothing>> =
        controller {
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
        controller {
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