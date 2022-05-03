package com.iplease.server.ip.release.global.event.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class RabbitMqEventPublishService(
    val rabbitTemplate: RabbitTemplate
): EventPublishService {
    override fun <T: Any> publish(routingKey: String, data: T): T =
        ObjectMapper().writeValueAsString(data)
            .let { rabbitTemplate.convertAndSend(routingKey, it) }
            .let { data }
}