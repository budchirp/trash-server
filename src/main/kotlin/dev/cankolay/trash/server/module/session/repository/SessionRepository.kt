package dev.cankolay.trash.server.module.session.repository

import dev.cankolay.trash.server.module.session.entity.Session
import org.springframework.data.jpa.repository.JpaRepository

interface SessionRepository : JpaRepository<Session, String> {
    fun existsByTokenAndUserId(token: String, user: String): Boolean
    fun deleteByTokenAndUserId(token: String, user: String): Long
    fun deleteAllByUserId(user: String): Long
    fun findAllByUserId(user: String): List<Session>
    fun findByTokenAndUserId(token: String, user: String): Session?
}