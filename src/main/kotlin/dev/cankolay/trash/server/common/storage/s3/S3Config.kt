package dev.cankolay.trash.server.common.storage.s3

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
@EnableConfigurationProperties(S3Properties::class)
class S3Config(private val properties: S3Properties) {

    private fun credentialsProvider(): AwsCredentialsProvider {
        val accessKey = properties.accessKeyId
        val secretKey = properties.secretAccessKey

        return if (!accessKey.isNullOrBlank() && !secretKey.isNullOrBlank()) {
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
        } else {
            DefaultCredentialsProvider.create()
        }
    }

    @Bean
    fun s3Client(): S3Client {
        val builder = S3Client.builder()
            .region(Region.of(properties.region))
            .credentialsProvider(credentialsProvider())

        properties.endpoint?.takeIf { it.toString().isNotBlank() }?.let {
            builder.endpointOverride(it)
            builder.serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            )
        }

        return builder.build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val builder = S3Presigner.builder()
            .region(Region.of(properties.region))
            .credentialsProvider(credentialsProvider())

        properties.endpoint?.takeIf { it.toString().isNotBlank() }?.let {
            builder.endpointOverride(it)
            builder.serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            )
        }

        return builder.build()
    }
}
