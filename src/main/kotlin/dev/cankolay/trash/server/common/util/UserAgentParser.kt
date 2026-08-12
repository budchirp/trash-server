package dev.cankolay.trash.server.common.util

import dev.cankolay.trash.server.common.model.UserAgent
import dev.cankolay.trash.server.common.model.UserAgentPlatform
import nl.basjes.parse.useragent.UserAgentAnalyzer
import org.springframework.stereotype.Component

@Component
class UserAgentParser {
    private val userAgentAnalyzer = UserAgentAnalyzer
        .newBuilder()
        .withField("DeviceName")
        .withField("DeviceClass")
        .withField("OperatingSystemNameVersion")
        .withField("AgentNameVersion")
        .build()

    operator fun invoke(userAgent: String) = userAgentAnalyzer.parse(userAgent).let {
        UserAgent(
            device = it.getValue("DeviceName") ?: "Unknown",
            platform = when (it.getValue("DeviceClass")) {
                "Desktop" -> UserAgentPlatform.DESKTOP
                "Phone", "Console" -> UserAgentPlatform.MOBILE
                "Tablet" -> UserAgentPlatform.TABLET
                else -> UserAgentPlatform.DESKTOP
            },

            os = it.getValue("OperatingSystemNameVersion") ?: "Unknown",
            browser = it.getValue("AgentNameVersion") ?: "Unknown",
        )
    }
}
