package dev.cankolay.trash.server.common.storage.exception

import dev.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class InvalidContentTypeException(
    message: String = "Invalid content type"
) : ApiException(
    status = HttpStatus.BAD_REQUEST,
    code = "invalid_content_type",
    message = message
)
