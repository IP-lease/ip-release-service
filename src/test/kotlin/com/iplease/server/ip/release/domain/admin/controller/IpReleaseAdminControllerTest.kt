package com.iplease.server.ip.release.domain.admin.controller

import com.iplease.server.ip.release.global.admin.data.dto.IpReleaseAcceptDto
import com.iplease.server.ip.release.domain.admin.service.IpReleaseAdminService
import com.iplease.server.ip.release.global.common.data.type.Permission
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandQueryService
import com.iplease.server.ip.release.infra.event.service.EventPublishService
import com.iplease.server.ip.release.infra.event.type.Event
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.infra.policy.service.PolicyCheckService
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

    //IP ?????? ?????? ?????? ??????
    //IP_RELEASE_ACCEPT ????????? ???????????????, -> PolicyCheckService ??? ??????
    //????????? ????????? ?????????????????? -> PolicyCheckService ??? ??????
    //?????? ????????? ????????? ???????????????(Acceptable) ??????????????????. -> Service ?????? ??????

    //IP ?????? ?????? ?????????
    //?????? Demand??? ??????(??????)????????? -> Service ?????? ??????
    // IP???????????? ???????????? ????????????. -> EventPublishService ??? ??????
    @Test @DisplayName("IP ?????? ?????? ?????? - ?????? ??????")
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