package dev.cankolay.trash.server.common.storage.s3

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(prefix = "storage.s3")
data class S3Properties(
    var accessKeyId: String? = null,
    var secretAccessKey: String? = null,

    var bucket: String = "trash-storage",
    var region: String = "us-east-1",

    var endpoint: URI? = null
)
