package com.cankolay.trash.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TrashCoreApplication

fun main(args: Array<String>) {
    runApplication<TrashCoreApplication>(*args)
}
