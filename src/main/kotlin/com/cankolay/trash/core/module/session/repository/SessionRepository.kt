package com.cankolay.trash.core.module.session.repository

import com.cankolay.trash.core.module.session.entity.Session
import org.springframework.data.jpa.repository.JpaRepository

interface SessionRepository : JpaRepository<Session, String> {
    fun existsByTokenAndUserId(token: String, user: Long): Boolean
    fun deleteByTokenAndUserId(token: String, user: Long): Long
    fun deleteAllByUserId(user: Long): Long
    fun findAllByUserId(user: Long): List<Session>
    fun findByTokenAndUserId(token: String, user: Long): Session?
}