package com.iplease.server.ip.release

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
class IpReleaseServiceApplication {
    @Bean
    fun webClientBuilder(function: ReactorLoadBalancerExchangeFilterFunction): WebClient.Builder =
        WebClient.builder()
            .filter(function)
}

fun main(args: Array<String>) {
    runApplication<IpReleaseServiceApplication>(*args)
}
