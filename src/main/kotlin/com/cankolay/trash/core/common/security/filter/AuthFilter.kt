package com.cankolay.trash.core.common.security.filter

import com.cankolay.trash.core.common.security.annotation.Authenticate
import com.cankolay.trash.core.common.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.context.support.WebApplicationContextUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@Component
class AuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val handler = getHandler(request) ?: return true
        return !(handler.method.isAnnotationPresent(Authenticate::class.java)
                || handler.beanType.isAnnotationPresent(Authenticate::class.java))
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtService.extract(token = request.getHeader("Authorization"))
        if (token == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing JWT")
            return
        }

        if (!jwtService.verify(jwt = token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT")
            return
        }

        val payload = jwtService.payload(token)

        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
            payload.id,
            payload.token,
            emptyList()
        )

        filterChain.doFilter(request, response)
    }

    private fun getHandler(request: HttpServletRequest): HandlerMethod? {
        if (RequestContextHolder.getRequestAttributes() !is ServletRequestAttributes) return null

        val context = WebApplicationContextUtils
            .getRequiredWebApplicationContext(request.servletContext)

        val handlerMapping = context.getBean("requestMappingHandlerMapping") as RequestMappingHandlerMapping
        return handlerMapping.getHandler(request)?.handler as? HandlerMethod
    }
}
