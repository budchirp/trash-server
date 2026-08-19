package dev.cankolay.trash.server.module.storage.s3

import dev.cankolay.trash.server.module.storage.ObjectStorage
import dev.cankolay.trash.server.module.storage.ObjectUploadRequest
import dev.cankolay.trash.server.module.storage.PresignedUpload
import dev.cankolay.trash.server.module.storage.exception.ObjectStorageException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URI

@Component
class S3ObjectStorage(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    private val properties: S3Properties
) : ObjectStorage {
    private val logger = LoggerFactory.getLogger(S3ObjectStorage::class.java)

    override fun createUpload(request: ObjectUploadRequest): PresignedUpload {
        try {
            val putRequest = PutObjectRequest.builder()
                .bucket(properties.bucket)
                .key(request.key)
                .contentType(request.contentType)

            request.contentLength?.let { putRequest.contentLength(it) }

            val presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(request.expiration)
                .putObjectRequest(putRequest.build())
                .build()

            val presignedPutObject = s3Presigner.presignPutObject(presignRequest)

            return PresignedUpload(
                url = presignedPutObject.url().toURI(),
                key = request.key,
                expiresAt = presignedPutObject.expiration()
            )
        } catch (e: Exception) {
            throw ObjectStorageException("Failed to generate presigned upload URL", e)
        }
    }

    override fun url(key: String): URI {
        val endpoint = properties.endpoint?.toString()
            ?.takeIf { it.isNotBlank() }
            ?.trimEnd('/')
        val baseUrl = endpoint ?: "https://${properties.bucket}.s3.${properties.region}.amazonaws.com"
        val objectPath = if (endpoint != null) {
            "${properties.bucket}/$key"
        } else {
            key
        }

        return URI.create("$baseUrl/$objectPath")
    }

    override fun delete(key: String) {
        try {
            val request = DeleteObjectRequest.builder()
                .bucket(properties.bucket)
                .key(key)
                .build()

            s3Client.deleteObject(request)
        } catch (_: NoSuchKeyException) {
            logger.debug("S3 object was already absent: {}", key)
        } catch (e: S3Exception) {
            if (e.statusCode() == 404) {
                logger.debug("S3 object was already absent: {}", key)
            } else {
                logger.error("Failed to delete S3 object: {}", key, e)
            }
        } catch (e: Exception) {
            logger.error("Failed to delete S3 object: {}", key, e)
        }
    }
}
