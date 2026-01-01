package dev.cankolay.trash.server.common.security.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(value = AnnotationRetention.RUNTIME)
annotation class Authenticate