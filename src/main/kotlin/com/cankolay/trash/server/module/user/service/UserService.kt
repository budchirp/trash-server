package com.cankolay.trash.server.module.user.service

import com.cankolay.trash.server.common.service.AuthService
import com.cankolay.trash.server.common.service.JwtService
import com.cankolay.trash.server.common.util.Encryptor
import com.cankolay.trash.server.module.session.exception.InvalidPasswordException
import com.cankolay.trash.server.module.session.exception.UnauthorizedException
import com.cankolay.trash.server.module.session.repository.SessionRepository
import com.cankolay.trash.server.module.user.entity.User
import com.cankolay.trash.server.module.user.exception.InvalidVerificationTokenException
import com.cankolay.trash.server.module.user.exception.UserExistsException
import com.cankolay.trash.server.module.user.repository.ProfileRepository
import com.cankolay.trash.server.module.user.repository.UserRepository
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
        val user = authService.user()
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
        val user = authService.user()

        if (!encryptor.check(password = password, encrypted = user.password)) {
            throw InvalidPasswordException()
        }

        return jwtService.generate(id = user.id, duration = 1000 * 60 * 15)
    }

    @Transactional
    @Throws(InvalidVerificationTokenException::class, UnauthorizedException::class)
    fun delete(token: String) {
        val user = authService.user()

        if (!jwtService.verify(jwt = token)) {
            throw InvalidVerificationTokenException()
        }

        if (user.id != jwtService.id(jwt = token)) {
            throw UnauthorizedException()
        }

        sessionRepository.deleteAllByUserId(user = user.id)
        profileRepository.deleteById(user.profile.id)

        userRepository.deleteByUsername(username = user.username)
    }
}