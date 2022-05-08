package com.iplease.server.ip.release.infra.event.service

import com.iplease.server.ip.release.infra.event.listener.RabbitEventListener
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class RabbitMqEventSubscribeService: EventSubscribeService, RabbitEventListener {
    private val list = mutableSetOf<RabbitEventListener>()

    @RabbitListener(queues = ["server.ip.release"])
    override fun handle(@Payload event: String, message: Message) = list.forEach { it.handle(event, message) }
    override fun addListener(listener: RabbitEventListener) = list.add(listener).let { }
}