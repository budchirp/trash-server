package dev.cankolay.trash.server.module.application.exception

import dev.cankolay.trash.server.common.exception.ApiException
import org.springframework.http.HttpStatus

class DeveloperProgramRequiredException :
    ApiException(
        status = HttpStatus.FORBIDDEN,
        code = "developer_program_required",
        message = "Enroll in the developer program to create applications"
    )
