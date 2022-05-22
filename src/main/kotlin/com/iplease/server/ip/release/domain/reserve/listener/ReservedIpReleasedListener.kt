package com.iplease.server.ip.release.domain.reserve.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.iplease.lib.messa.event.data.ip.release.IpReleaseSuccessEvent
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.infra.event.service.EventSubscribeService
import com.iplease.server.ip.release.infra.event.type.Event
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.stereotype.Component

@Component
class ReservedIpReleasedListener(
    private val ipReleaseReserveRepository: IpReleaseReserveRepository,
    eventSubscribeService: EventSubscribeService
): com.iplease.server.ip.release.infra.event.listener.RabbitEventListener {
    init { eventSubscribeService.addListener(this ) }
    val LOGGER = LoggerFactory.getLogger(this::class.java)
    val objectMapper = ObjectMapper().registerKotlinModule()

    override fun handle(event: String, message: Message) {
        if(message.messageProperties.receivedRoutingKey != Event.IP_RELEASED.routingKey) return
        val dto = objectMapper.readValue(event, IpReleaseSuccessEvent::class.java)
        LOGGER.info("assignedIpUuid가 ${dto.assignedIpUuid}인 신청을 제거합니다")
        ipReleaseReserveRepository.deleteAllByAssignedIpUuid(dto.assignedIpUuid)
            .subscribe() //오류가 발생하지 않는것을 전제로 한다.
    }
}