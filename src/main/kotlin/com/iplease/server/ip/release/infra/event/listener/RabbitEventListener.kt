package com.iplease.server.ip.release.infra.event.listener

import org.springframework.amqp.core.Message

interface RabbitEventListener {
    fun handle(event: String, message: Message)
}