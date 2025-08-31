package com.cankolay.trash.server.module.user.repository

import com.cankolay.trash.server.module.user.entity.Profile
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileRepository : JpaRepository<Profile, Long>