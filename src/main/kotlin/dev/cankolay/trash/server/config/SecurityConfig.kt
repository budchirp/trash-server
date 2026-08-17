package dev.cankolay.trash.server.config

import dev.cankolay.trash.server.module.auth.filter.JwtAuthenticationFilter
import dev.cankolay.trash.server.module.auth.security.ApiAccessDeniedHandler
import dev.cankolay.trash.server.module.auth.security.ApiAuthenticationEntryPoint
import dev.cankolay.trash.server.module.security.PermissionKeys
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter

@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationEntryPoint: ApiAuthenticationEntryPoint,
    private val accessDeniedHandler: ApiAccessDeniedHandler
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors { }
        http.csrf { it.disable() }

        http.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        http.headers {
            it.frameOptions { frameOptions -> frameOptions.deny() }
            it.referrerPolicy { referrerPolicy ->
                referrerPolicy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)
            }
        }

        http.exceptionHandling {
            it.authenticationEntryPoint(authenticationEntryPoint)
            it.accessDeniedHandler(accessDeniedHandler)
        }

        http.authorizeHttpRequests {
            it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            it.requestMatchers(HttpMethod.POST, "/user").permitAll()
            it.requestMatchers(HttpMethod.POST, "/session").permitAll()
            it.requestMatchers(HttpMethod.GET, "/server/version").permitAll()
            it.requestMatchers(
                "/error",
                "/openapi.json",
                "/swagger",
                "/swagger/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/scalar",
                "/scalar/**"
            ).permitAll()

            it.requestMatchers(HttpMethod.GET, "/user", "/user/*").hasAuthority(PermissionKeys.USER_READ)
            it.requestMatchers(HttpMethod.DELETE, "/user").hasAuthority(PermissionKeys.USER_DELETE)
            it.requestMatchers(HttpMethod.PATCH, "/user/profile").hasAuthority(PermissionKeys.PROFILE_UPDATE)
            it.requestMatchers(HttpMethod.POST, "/user/profile/picture").hasAuthority(PermissionKeys.PROFILE_UPDATE)
            it.requestMatchers(HttpMethod.DELETE, "/user/profile/picture").hasAuthority(PermissionKeys.PROFILE_UPDATE)

            it.requestMatchers(HttpMethod.GET, "/session", "/session/*", "/session/all")
                .hasAuthority(PermissionKeys.SESSION_READ)
            it.requestMatchers(HttpMethod.DELETE, "/session", "/session/*")
                .hasAuthority(PermissionKeys.SESSION_DELETE)

            it.requestMatchers(HttpMethod.POST, "/security/token", "/security/token/verify").authenticated()

            it.requestMatchers(HttpMethod.POST, "/application").hasAuthority(PermissionKeys.APPLICATION_CREATE)
            it.requestMatchers(HttpMethod.GET, "/application/*", "/application/all")
                .hasAuthority(PermissionKeys.APPLICATION_READ)
            it.requestMatchers(HttpMethod.DELETE, "/application/*").hasAuthority(PermissionKeys.APPLICATION_DELETE)

            it.requestMatchers(HttpMethod.POST, "/connection/connect").hasAuthority(PermissionKeys.CONNECTION_CREATE)
            it.requestMatchers(HttpMethod.GET, "/connection/*", "/connection/all")
                .hasAuthority(PermissionKeys.CONNECTION_READ)
            it.requestMatchers(HttpMethod.DELETE, "/connection/*").hasAuthority(PermissionKeys.CONNECTION_DELETE)

            it.anyRequest().denyAll()
        }

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
