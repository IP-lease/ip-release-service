package com.iplease.server.ip.release.global.event.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class RabbitMqEventPublishService(
    val rabbitTemplate: RabbitTemplate
): EventPublishService {
    companion object { const val EXCHANGE_NAME = "iplease.event" }
    override fun <T: Any> publish(routingKey: String, data: T): T =
        ObjectMapper().writeValueAsString(data)
            .let { rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, it) }
            .let { data }
}