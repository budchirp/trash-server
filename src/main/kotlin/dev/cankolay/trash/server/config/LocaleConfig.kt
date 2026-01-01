package dev.cankolay.trash.server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver

@Configuration
class LocaleConfig {
    @Bean
    fun localeResolver(): LocaleResolver = AcceptHeaderLocaleResolver()
}
