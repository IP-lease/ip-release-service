package com.iplease.server.ip.release.global.event.service

import com.iplease.server.ip.release.global.event.listener.RabbitEventListener
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class RabbitMqEventSubscribeService {
    private val list = mutableSetOf<RabbitEventListener>()

    @RabbitListener(queues = ["server.ip.release"])
    fun handle(@Payload event: String, message: Message) = list.forEach { it.handle(event, message) }
    fun addListener(listener: RabbitEventListener) = list.add(listener).let { }
}