package dev.cankolay.trash.server.common.service

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimiter {
    private val buckets = ConcurrentHashMap<String, Bucket>()

    fun check(key: String, limit: Int = 5, window: Duration = DEFAULT_ATTEMPT_WINDOW): Boolean {
        val now = Instant.now()
        evictExpired(now)

        val bucket = buckets.compute(key) { _, current ->
            if (current == null || current.window != window || !now.isBefore(current.startedAt.plus(window))) {
                Bucket(startedAt = now, attempts = 1, window = window)
            } else {
                current.copy(attempts = current.attempts + 1)
            }
        } ?: return false

        return bucket.attempts <= limit
    }

    fun reset(key: String) {
        buckets.remove(key)
    }

    private fun evictExpired(now: Instant) {
        if (buckets.size < MAX_BUCKETS_BEFORE_CLEANUP) return

        buckets.entries.removeIf { (_, bucket) -> !now.isBefore(bucket.startedAt.plus(bucket.window)) }
    }

    private data class Bucket(
        val startedAt: Instant,
        val attempts: Int,
        val window: Duration
    )

    private companion object {
        const val MAX_BUCKETS_BEFORE_CLEANUP = 1_000

        val DEFAULT_ATTEMPT_WINDOW: Duration = Duration.ofMinutes(1)
    }
}
