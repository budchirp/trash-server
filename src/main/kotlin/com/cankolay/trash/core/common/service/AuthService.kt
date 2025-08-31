package com.cankolay.trash.core.common.service

import com.cankolay.trash.core.module.session.exception.UnauthorizedException
import com.cankolay.trash.core.module.session.exception.UserNotFoundException
import com.cankolay.trash.core.module.user.entity.User
import com.cankolay.trash.core.module.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
) {
    @Throws(UnauthorizedException::class, UserNotFoundException::class)
    fun user(): User {
        val auth = SecurityContextHolder.getContext().authentication
        val id = auth?.principal as? String ?: throw UnauthorizedException()

        return userRepository.findById(id).orElseThrow { UserNotFoundException() }
    }

    fun token(): String {
        val auth = SecurityContextHolder.getContext().authentication
        return auth?.credentials as? String ?: throw UnauthorizedException()
    }
}