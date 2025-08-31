package com.cankolay.trash.server.common.service

import nl.basjes.parse.useragent.UserAgentAnalyzer
import org.springframework.stereotype.Component

enum class UserAgentPlatform {
    MOBILE, DESKTOP, TABLET
}

data class UserAgent(
    val device: String,
    val platform: UserAgentPlatform,

    val os: String,
    val browser: String,
)

@Component
class UserAgentParser {
    private val userAgentAnalyzer = UserAgentAnalyzer
        .newBuilder()
        .withField("DeviceName")
        .withField("DeviceClass")
        .withField("OperatingSystemName")
        .withField("AgentNameVersion")
        .build()

    operator fun invoke(userAgent: String) = userAgentAnalyzer.parse(userAgent).let {
        UserAgent(
            device = it.getValue("DeviceName"),
            platform = when (it.getValue("DeviceClass")) {
                "Desktop" -> UserAgentPlatform.DESKTOP
                "Phone", "Console" -> UserAgentPlatform.MOBILE
                "Tablet" -> UserAgentPlatform.TABLET
                else -> UserAgentPlatform.DESKTOP
            },

            os = it.getValue("OperatingSystemName"),
            browser = it.getValue("AgentNameVersion"),
        )
    }
}