package com.iplease.server.ip.release.global.log

import com.iplease.server.ip.release.global.event.listener.RabbitEventListener
import com.iplease.server.ip.release.global.event.service.EventSubscribeService
import com.iplease.server.ip.release.global.log.service.LoggingService
import com.iplease.server.ip.release.global.log.type.LoggingActType
import com.iplease.server.ip.release.global.log.util.EventSubscribeInput
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class LoggingEventListener(
    eventSubscribeService: EventSubscribeService,
    private val loggingService: LoggingService
): RabbitEventListener {
    init {
        eventSubscribeService.addListener(this)
    }
    override fun handle(event: String, message: Message) {
        loggingService.withLog(
            EventSubscribeInput(message.messageProperties.receivedRoutingKey, event),
            Unit.toMono(),
            LoggingActType.EVENT_SUBSCRIBE_LOGGER
        ).block()
    }
}