package com.iplease.server.ip.release.domain.admin.controller

import com.iplease.server.ip.release.global.admin.data.dto.IpReleaseAcceptDto
import com.iplease.server.ip.release.domain.admin.service.IpReleaseAdminService
import com.iplease.server.ip.release.global.common.data.type.Permission
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandQueryService
import com.iplease.server.ip.release.global.event.service.EventPublishService
import com.iplease.server.ip.release.global.event.type.Event
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.global.policy.service.PolicyCheckService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleaseAdminControllerTest {
    private lateinit var target: IpReleaseAdminController
    private lateinit var ipReleaseAdminService: IpReleaseAdminService
    private lateinit var ipReleaseDemandQueryService: IpReleaseDemandQueryService
    private lateinit var eventPublishService: EventPublishService
    private lateinit var policyCheckService: PolicyCheckService
    private var demandUuid by Delegates.notNull<Long>()
    private var operatorUuid by Delegates.notNull<Long>()
    private val monoJustAny = Mono.just(Any())
    @BeforeEach
    fun setUp() {
        ipReleaseAdminService = mock()
        ipReleaseDemandQueryService = mock()
        eventPublishService = mock()
        policyCheckService = mock() {
            on { checkDemandExists(any()) } doReturn monoJustAny
            on { checkDemandAccess(any(), any()) } doReturn monoJustAny
            on { checkPermission(any(), any()) } doReturn monoJustAny
            on { checkAssignedIpExists(any())} doReturn monoJustAny
            on { checkAssignedIpAccess(any(), any()) } doReturn monoJustAny
        }

        target = IpReleaseAdminController(ipReleaseAdminService, eventPublishService, policyCheckService)

        demandUuid = Random.nextLong()
        operatorUuid = Random.nextLong()
    }

    //IP 할당 해제 수락 조건
    //IP_RELEASE_ACCEPT 권한이 있어야하며, -> PolicyCheckService 에 위임
    //수락할 신청이 존재해야하며 -> PolicyCheckService 에 위임
    //해당 신청의 상태가 수락가능한(Acceptable) 상태여야한다. -> Service 단에 위임

    //IP 할당 해제 수락시
    //해당 Demand를 수락(삭제)시키고 -> Service 단에 위임
    // IP할당해제 이벤트를 발행한다. -> EventPublishService 에 위임
    @Test @DisplayName("IP 할당 해제 수락 - 수락 성공")
    fun acceptReleaseIpSuccess() {
        val assignedIpUuid = Random.nextLong()
        val dto = IpReleaseAcceptDto(assignedIpUuid, demandUuid, operatorUuid)
        val role = Role.values().filter { it.hasPermission(Permission.IP_RELEASE_ACCEPT) }.random()

        whenever(ipReleaseDemandQueryService.existsDemandByUuid(any())).thenReturn(true.toMono())
        whenever(ipReleaseAdminService.acceptDemand(demandUuid, operatorUuid)).thenReturn(dto.toMono())
        whenever(eventPublishService.publish(Event.IP_RELEASED.routingKey, dto)).thenReturn(dto)

        target.acceptReleaseIp(demandUuid, operatorUuid, role).block()!!

        verify(ipReleaseAdminService, times(1)).acceptDemand(demandUuid, operatorUuid)
        verify(eventPublishService, times(1)).publish(Event.IP_RELEASED.routingKey, dto)
    }
}