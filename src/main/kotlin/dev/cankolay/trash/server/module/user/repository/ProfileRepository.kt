package dev.cankolay.trash.server.module.user.repository

import dev.cankolay.trash.server.module.user.entity.Profile
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileRepository : JpaRepository<Profile, Long>