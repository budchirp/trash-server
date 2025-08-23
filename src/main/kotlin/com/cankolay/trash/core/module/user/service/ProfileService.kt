package com.cankolay.trash.core.module.user.service

import com.cankolay.trash.core.common.service.AuthService
import com.cankolay.trash.core.module.user.dto.UserDto
import com.cankolay.trash.core.module.user.entity.Profile
import com.cankolay.trash.core.module.user.entity.toDto
import com.cankolay.trash.core.module.user.repository.ProfileRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ProfileService(
    private val profileRepository: ProfileRepository,
    private val authService: AuthService
) {
    @Transactional
    fun create(name: String): Profile {
        return profileRepository.save(Profile(name = name))
    }

    fun get(): UserDto {
        val user = authService.getUser()
        return user.copy().toDto()
    }

    @Transactional
    fun update(name: String?) {
        val user = authService.getUser()

        user.profile.name = name ?: user.profile.name
        profileRepository.save(user.profile)
    }
}