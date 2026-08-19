package dev.cankolay.trash.server.module.security

object PermissionKeys {
    const val WILDCARD = "*"

    const val USER_READ = "user:read"
    const val USER_DELETE = "user:delete"

    const val PROFILE_UPDATE = "profile:update"

    const val SESSION_READ = "session:read"
    const val SESSION_DELETE = "session:delete"

    const val APPLICATION_READ = "application:read"
    const val APPLICATION_CREATE = "application:create"
    const val APPLICATION_UPDATE = "application:update"
    const val APPLICATION_DELETE = "application:delete"

    const val CONNECTION_READ = "connection:read"
    const val CONNECTION_CREATE = "connection:create"
    const val CONNECTION_DELETE = "connection:delete"

    val ALL: Set<String> = setOf(
        USER_READ,
        USER_DELETE,
        PROFILE_UPDATE,
        SESSION_READ,
        SESSION_DELETE,
        APPLICATION_READ,
        APPLICATION_CREATE,
        APPLICATION_UPDATE,
        APPLICATION_DELETE,
        CONNECTION_READ,
        CONNECTION_CREATE,
        CONNECTION_DELETE,
        WILDCARD
    )
}
