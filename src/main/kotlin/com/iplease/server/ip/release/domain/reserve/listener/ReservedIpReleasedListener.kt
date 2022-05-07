package com.iplease.server.ip.release.domain.reserve.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.admin.data.dto.IpReleaseAcceptDto
import com.iplease.server.ip.release.global.event.listener.RabbitEventListener
import com.iplease.server.ip.release.global.event.service.EventSubscribeService
import com.iplease.server.ip.release.global.event.type.Event
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component

@Component
class ReservedIpReleasedListener(
    private val ipReleaseReserveRepository: IpReleaseReserveRepository,
    eventSubscribeService: EventSubscribeService
): RabbitEventListener {
    init { eventSubscribeService.addListener(this ) }
    val LOGGER = LoggerFactory.getLogger(this::class.java)

    override fun handle(event: String, message: Message) {
        if(message.messageProperties.receivedRoutingKey != Event.IP_RELEASED.routingKey) return
        val dto = ObjectMapper().registerModule(KotlinModule()).readValue(event, IpReleaseAcceptDto::class.java)
        LOGGER.info("assignedIpUuid가 ${dto.assignedIpUuid}인 신청을 제거합니다")
        ipReleaseReserveRepository.deleteAllByAssignedIpUuid(dto.assignedIpUuid)
            .subscribe()
    }
}