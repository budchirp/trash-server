package dev.cankolay.trash.server.common.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service


@Service
class RequestInfoService {
    fun getUserAgent(request: HttpServletRequest): String =
        request.getHeader("User-Agent")?.take(n = MAX_USER_AGENT_LENGTH) ?: "Unknown"

    fun getClientIp(request: HttpServletRequest): String = request.remoteAddr ?: "0.0.0.0"

    private companion object {
        const val MAX_USER_AGENT_LENGTH = 512
    }
}
