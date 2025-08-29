package com.cankolay.trash.core.module.user.service

import com.cankolay.trash.core.common.service.AuthService
import com.cankolay.trash.core.common.service.JwtService
import com.cankolay.trash.core.common.util.Encryptor
import com.cankolay.trash.core.module.session.exception.InvalidPasswordException
import com.cankolay.trash.core.module.session.exception.UnauthorizedException
import com.cankolay.trash.core.module.session.repository.SessionRepository
import com.cankolay.trash.core.module.user.entity.User
import com.cankolay.trash.core.module.user.exception.InvalidVerificationTokenException
import com.cankolay.trash.core.module.user.exception.UserExistsException
import com.cankolay.trash.core.module.user.repository.ProfileRepository
import com.cankolay.trash.core.module.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserService(
    private val profileService: ProfileService,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository,
    private val authService: AuthService,
    private val jwtService: JwtService,
    private val encryptor: Encryptor
) {
    fun get(): User {
        val user = authService.getUser()
        
        return user.copy()
    }

    @Transactional
    @Throws(UserExistsException::class)
    fun create(email: String, username: String, password: String): User {
        userRepository.findByUsername(username)?.let {
            throw UserExistsException()
        }

        val profile = profileService.create()

        return userRepository.save(
            User(
                email = email,
                username = username,
                password = encryptor.encrypt(password),
                profile = profile
            )
        )
    }

    @Throws(InvalidPasswordException::class)
    fun createToken(password: String): String {
        val user = authService.getUser()

        if (!encryptor.check(password = password, encrypted = user.password)) {
            throw InvalidPasswordException()
        }

        return jwtService.generate(id = user.id)
    }

    @Transactional
    @Throws(InvalidVerificationTokenException::class, UnauthorizedException::class)
    fun delete(token: String) {
        val user = authService.getUser()

        if (!jwtService.verify(jwt = token)) {
            throw InvalidVerificationTokenException()
        }

        val id = jwtService.getId(jwt = token)
        if (user.id != id) {
            throw UnauthorizedException()
        }

        sessionRepository.deleteAllByUserId(user = user.id)
        profileRepository.deleteById(user.profile.id)

        userRepository.deleteByUsername(username = user.username)
    }
}