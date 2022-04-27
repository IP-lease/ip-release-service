package com.iplease.server.ip.release

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IpReleaseServiceApplication

fun main(args: Array<String>) {
    runApplication<IpReleaseServiceApplication>(*args)
}
