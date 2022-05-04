package com.iplease.server.ip.release.domain.admin.controller

import com.iplease.server.ip.release.domain.admin.dto.IpReleaseAcceptDto
import com.iplease.server.ip.release.domain.admin.service.IpReleaseAdminService
import com.iplease.server.ip.release.global.demand.exception.UnknownDemandException
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandQueryService
import com.iplease.server.ip.release.global.event.service.EventPublishService
import com.iplease.server.ip.release.global.event.type.Event
import com.iplease.server.ip.release.global.common.exception.PermissionDeniedException
import com.iplease.server.ip.release.global.common.type.Permission
import com.iplease.server.ip.release.global.common.type.Role
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import reactor.kotlin.core.publisher.toMono
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleaseAdminControllerTest {
    private lateinit var target: IpReleaseAdminController
    private lateinit var ipReleaseAdminService: IpReleaseAdminService
    private lateinit var ipReleaseDemandQueryService: IpReleaseDemandQueryService
    private lateinit var eventPublishService: EventPublishService
    private var demandUuid by Delegates.notNull<Long>()
    private var operatorUuid by Delegates.notNull<Long>()
    @BeforeEach
    fun setUp() {
        ipReleaseAdminService = mock()
        ipReleaseDemandQueryService = mock()
        eventPublishService = mock()

        target = IpReleaseAdminController(ipReleaseAdminService, ipReleaseDemandQueryService, eventPublishService)

        demandUuid = Random.nextLong()
        operatorUuid = Random.nextLong()
    }

    //IP 할당 해제 수락 조건
    //IP_RELEASE_ACCEPT 권한이 있어야하며,
    //수락할 신청이 존재해야하며
    //해당 신청의 상태가 수락가능한(Acceptable) 상태여야한다. -> Service 단에 위임

    //IP 할당 해제 수락시
    //해당 Demand를 수락(삭제)시키고 -> Service 단에 위임
    // IP할당해제 이벤트를 발행한다.
    @Test @DisplayName("IP 할당 해제 수락 - 수락 성공")
    fun acceptReleaseIpSuccess() {
        val dto = IpReleaseAcceptDto(demandUuid, operatorUuid)

        whenever(ipReleaseDemandQueryService.existsDemandByUuid(any())).thenReturn(true.toMono())
        whenever(ipReleaseAdminService.acceptDemand(demandUuid, operatorUuid)).thenReturn(dto.toMono())
        whenever(eventPublishService.publish(Event.IP_RELEASED.routingKey, dto)).thenReturn(dto)

        target.acceptReleaseIp(demandUuid, operatorUuid, Role.ADMINISTRATOR).block()!!

        verify(ipReleaseAdminService, times(1)).acceptDemand(demandUuid, operatorUuid)
        verify(eventPublishService, times(1)).publish(Event.IP_RELEASED.routingKey, dto)
    }

    @Test @DisplayName("IP 할당 해제 수락 - 권한이 없을경우")
   fun acceptReleaseIpFailurePermissionDenied() {
        val dto = IpReleaseAcceptDto(demandUuid, operatorUuid)

        whenever(ipReleaseDemandQueryService.existsDemandByUuid(any())).thenReturn(true.toMono())

       val exception = assertThrows<PermissionDeniedException> { target.acceptReleaseIp(demandUuid, operatorUuid, Role.GUEST).block()!! }

        assert(exception.permission == Permission.IP_RELEASE_ACCEPT)
        verify(ipReleaseAdminService, times(0)).acceptDemand(demandUuid, operatorUuid)
        verify(eventPublishService, times(0)).publish(Event.IP_RELEASED.routingKey, dto)
    }

    @Test @DisplayName("IP 할당 해제 수락 - 신청이 존재하지 않을경우")
    fun acceptReleaseIpFailureDemandNotExist() {
        val dto = IpReleaseAcceptDto(demandUuid, operatorUuid)

        whenever(ipReleaseDemandQueryService.existsDemandByUuid(any())).thenReturn(false.toMono())

        val exception = assertThrows<UnknownDemandException> { target.acceptReleaseIp(demandUuid, operatorUuid, Role.ADMINISTRATOR).block()!! }

        assert(exception.uuid == demandUuid)
        verify(ipReleaseAdminService, times(0)).acceptDemand(demandUuid, operatorUuid)
        verify(eventPublishService, times(0)).publish(Event.IP_RELEASED.routingKey, dto)
    }
}