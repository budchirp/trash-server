package com.cankolay.trash.core.common.service

import org.springframework.context.MessageSource
import org.springframework.stereotype.Service

@Service
class I18nService(private val messageSource: MessageSource, private val localeService: LocaleService) {
    fun get(key: String): String = try {
        messageSource.getMessage(key, null, localeService.get())
    } catch (_: Exception) {
        key
    }
}