package dev.cankolay.trash.server.common.storage.exception

import dev.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

open class ObjectStorageException(
    message: String = "Storage operation failed",
    cause: Throwable? = null
) : ApiException(
    status = HttpStatus.INTERNAL_SERVER_ERROR,
    code = "storage_error",
    message = message,
    cause = cause
)
