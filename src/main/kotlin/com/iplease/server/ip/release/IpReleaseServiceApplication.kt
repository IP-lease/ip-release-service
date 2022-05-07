package com.iplease.server.ip.release

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
class IpReleaseServiceApplication

fun main(args: Array<String>) {
    runApplication<IpReleaseServiceApplication>(*args)
}
