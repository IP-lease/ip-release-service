package com.iplease.server.ip.release.domain.demand.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.release.global.admin.data.dto.IpReleaseAcceptDto
import com.iplease.server.ip.release.global.demand.repository.IpReleaseDemandRepository
import com.iplease.server.ip.release.global.event.listener.RabbitEventListener
import com.iplease.server.ip.release.global.event.service.RabbitMqEventSubscribeService
import com.iplease.server.ip.release.global.event.type.Event
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component

@Component
class DemandedIpReleasedListener(
    private val ipReleaseDemandRepository: IpReleaseDemandRepository,
    rabbitMqEventSubscribeService: RabbitMqEventSubscribeService
): RabbitEventListener {
    init { rabbitMqEventSubscribeService.addListener(this ) }
    val LOGGER = LoggerFactory.getLogger(this::class.java)

    override fun handle(event: String, message: Message) {
        if(message.messageProperties.receivedRoutingKey != Event.IP_RELEASED.routingKey) return
        val dto = ObjectMapper().registerModule(KotlinModule()).readValue(event, IpReleaseAcceptDto::class.java)
        LOGGER.info("assignedIpUuid가 ${dto.assignedIpUuid}인 에약을 제거합니다")
        ipReleaseDemandRepository.deleteAllByAssignedIpUuid(dto.assignedIpUuid)
            .subscribe()
    }
}