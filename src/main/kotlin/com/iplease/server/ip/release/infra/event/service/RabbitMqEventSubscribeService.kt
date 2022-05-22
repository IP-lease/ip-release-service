package com.iplease.server.ip.release.infra.event.service

import com.iplease.server.ip.release.infra.event.listener.RabbitEventListener
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class RabbitMqEventSubscribeService: EventSubscribeService, RabbitEventListener {
    private val list = mutableSetOf<RabbitEventListener>()
    val LOGGER = LoggerFactory.getLogger(this::class.java)

    @RabbitListener(queues = ["server.ip.release"])
    override fun handle(@Payload event: String, message: Message) {
        Flux.fromIterable(list)
            .map { it.handle(event, message) }
            .onErrorContinue { exception, _ -> LOGGER.error("메세지 구독중 오류가 발생했습니다!\n" + exception.message)}
            .subscribe()
    }

    override fun addListener(listener: RabbitEventListener) = list.add(listener).let { }
}