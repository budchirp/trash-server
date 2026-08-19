package dev.cankolay.trash.server.module.user.service

import dev.cankolay.trash.server.module.auth.service.AuthService
import dev.cankolay.trash.server.module.user.entity.Profile
import dev.cankolay.trash.server.module.user.entity.ProfileGender
import dev.cankolay.trash.server.module.user.exception.InvalidProfileGenderException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileService(
    private val auth: AuthService,
) {
    @Transactional
    fun update(
        name: String? = null,
        gender: String? = null,
        `public`: Boolean? = null,
        dev: Boolean? = null
    ): Profile {
        val user = auth.user()

        user.profile.name = name ?: user.profile.name
        user.profile.gender = gender?.let {
            ProfileGender.fromValue(value = it) ?: throw InvalidProfileGenderException()
        } ?: user.profile.gender
        user.profile.`public` = `public` ?: user.profile.`public`
        user.profile.dev = dev ?: user.profile.dev

        return user.profile
    }
}
