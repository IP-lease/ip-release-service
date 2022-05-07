package com.iplease.server.ip.release.global.event.listener

import org.springframework.amqp.core.Message

interface RabbitEventListener {
    fun handle(event: String, message: Message)
}