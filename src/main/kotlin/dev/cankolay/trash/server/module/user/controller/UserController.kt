package dev.cankolay.trash.server.module.user.controller

import dev.cankolay.trash.server.common.model.ApiResponse
import dev.cankolay.trash.server.common.service.RequestInfoService
import dev.cankolay.trash.server.common.web.ApiResponseFactory
import dev.cankolay.trash.server.module.user.dto.UserDto
import dev.cankolay.trash.server.module.user.dto.request.CreateUserRequestDto
import dev.cankolay.trash.server.module.user.dto.request.DeleteUserRequestDto
import dev.cankolay.trash.server.module.user.mapper.toDto
import dev.cankolay.trash.server.module.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val responses: ApiResponseFactory,
    private val userService: UserService,
    private val requestInfoService: RequestInfoService
) {
    @PostMapping
    fun create(
        request: HttpServletRequest,
        @Valid @RequestBody body: CreateUserRequestDto
    ): ResponseEntity<ApiResponse<UserDto>> =
        responses.created(
            data = userService.create(
                email = body.email,
                username = body.username,
                password = body.password,
                ip = requestInfoService.getClientIp(request = request)
            ).toDto()
        )

    @GetMapping
    fun get(): ResponseEntity<ApiResponse<UserDto>> =
        responses.ok(data = userService.get().toDto())

    @DeleteMapping
    fun delete(@Valid @RequestBody body: DeleteUserRequestDto): ResponseEntity<ApiResponse<Nothing>> {
        userService.delete(securityToken = body.token)
        return responses.ok()
    }
}
