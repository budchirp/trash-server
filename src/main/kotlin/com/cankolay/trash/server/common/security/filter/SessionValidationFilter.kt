package com.cankolay.trash.server.common.security.filter

import com.cankolay.trash.server.module.session.service.SessionService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SessionValidationFilter(private val sessionService: SessionService) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth is AnonymousAuthenticationToken) {
            filterChain.doFilter(request, response)
            return
        }
        
        val id = auth?.principal as? String
        val token = auth?.credentials as? String

        if (id != null && token != null) {
            val session = sessionService.exists(user = id, token = token)
            if (!session) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid session")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}