package dev.cankolay.trash.server.module.auth.repository

import dev.cankolay.trash.server.module.auth.entity.Token
import dev.cankolay.trash.server.module.auth.entity.TokenType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface TokenRepository : JpaRepository<Token, String> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        "delete from Token token " +
                "where token.id = :id and token.type = :type and token.expiresAt > :now"
    )
    fun consume(
        @Param("id") id: String,
        @Param("type") type: TokenType,
        @Param("now") now: Instant
    ): Int
}
