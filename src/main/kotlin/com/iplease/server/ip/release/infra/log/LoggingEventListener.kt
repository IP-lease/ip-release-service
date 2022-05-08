package com.iplease.server.ip.release.infra.log

import com.iplease.server.ip.release.infra.log.service.LoggingService
import com.iplease.server.ip.release.infra.log.type.LoggingActType
import com.iplease.server.ip.release.infra.log.util.EventSubscribeInput
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class LoggingEventListener(
    eventSubscribeService: com.iplease.server.ip.release.infra.event.service.EventSubscribeService,
    private val loggingService: LoggingService
): com.iplease.server.ip.release.infra.event.listener.RabbitEventListener {
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