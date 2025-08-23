package com.cankolay.trash.core.module.user.repository

import com.cankolay.trash.core.module.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?

    fun deleteByUsername(username: String)
}