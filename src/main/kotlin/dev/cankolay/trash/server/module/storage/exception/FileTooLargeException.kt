package dev.cankolay.trash.server.module.storage.exception

import dev.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class FileTooLargeException(
    message: String = "File size exceeds limit"
) : ApiException(
    status = HttpStatus.BAD_REQUEST,
    code = "file_too_large",
    message = message
)
