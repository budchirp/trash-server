package dev.cankolay.trash.server.common.exception

import org.springframework.http.HttpStatus

class RateLimitedException :
    ApiException(status = HttpStatus.TOO_MANY_REQUESTS, code = "rate_limited", message = "Too many attempts")
