package com.cankolay.trash.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TrashServerApplication

fun main(args: Array<String>) {
    runApplication<TrashServerApplication>(*args)
}
