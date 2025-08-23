package com.cankolay.trash.core.common.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service


@Service
class RequestInfoService {

    fun getUserAgent(request: HttpServletRequest): String =
        request.getHeader("User-Agent") ?: "Unknown"

    fun getClientIp(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        return when {
            !xForwardedFor.isNullOrBlank() -> xForwardedFor.split(",")[0].trim()
            else -> request.remoteAddr ?: "0.0.0.0"
        }
    }
}