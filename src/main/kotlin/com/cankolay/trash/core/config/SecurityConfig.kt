package com.cankolay.trash.core.config

import com.cankolay.trash.core.common.security.filter.AuthFilter
import com.cankolay.trash.core.common.security.filter.SessionValidationFilter
import com.cankolay.trash.core.common.service.JwtService
import com.cankolay.trash.core.module.session.service.SessionService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class SecurityConfig(
    private val jwtService: JwtService,
    private val sessionService: SessionService
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable().cors().and()
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .addFilterBefore(
                SessionValidationFilter(sessionService = sessionService),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(AuthFilter(jwtService = jwtService), SessionValidationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOriginPatterns("*")
                    .allowedMethods("*")
                    .allowedHeaders("*")
                    .allowCredentials(true)
            }
        }
    }
}
