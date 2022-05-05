package com.iplease.server.ip.release.domain.demand.controller

import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.global.common.service.IpManageQueryService
import com.iplease.server.ip.release.domain.demand.service.IpReleaseDemandService
import com.iplease.server.ip.release.domain.demand.data.type.DemandStatusType
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandQueryService
import com.iplease.server.ip.release.global.common.type.Permission
import com.iplease.server.ip.release.global.common.type.Role
import com.iplease.server.ip.release.domain.demand.data.dto.AssignedIpDto
import com.iplease.server.ip.release.global.event.service.EventPublishService
import com.iplease.server.ip.release.global.event.type.Event
import com.iplease.server.ip.release.global.policy.service.PolicyCheckService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleaseDemandControllerTest {
    private lateinit var target: IpReleaseDemandController
    private lateinit var ipReleaseDemandService: IpReleaseDemandService
    private lateinit var ipReleaseDemandQueryService: IpReleaseDemandQueryService
    private lateinit var ipManageQueryService: IpManageQueryService
    private lateinit var eventPublishService: EventPublishService
    private lateinit var policyCheckService: PolicyCheckService
    private var assignedIpUuid by Delegates.notNull<Long>()
    private var issuerUuid by Delegates.notNull<Long>()
    private var demandUuid by Delegates.notNull<Long>()
    private lateinit var assignedIp: AssignedIpDto
    private val monoJustAny = Mono.just(Any())

    @BeforeEach
    fun setUp() {
        ipReleaseDemandService = mock()
        ipReleaseDemandQueryService = mock()
        ipManageQueryService = mock()
        eventPublishService = mock()
        policyCheckService = mock() {
            on { checkDemandExists(any()) } doReturn monoJustAny
            on { checkDemandAccess(any(), any()) } doReturn monoJustAny
            on { checkPermission(any(), any()) } doReturn monoJustAny
            on { checkAssignedIpExists(any())} doReturn monoJustAny
            on { checkAssignedIpAccess(any(), any()) } doReturn monoJustAny
        }

        target = IpReleaseDemandController(
            ipReleaseDemandService,
            eventPublishService,
            policyCheckService
        )

        issuerUuid = Random.nextLong()
        demandUuid = Random.nextLong()
        assignedIpUuid = Random.nextLong()
        assignedIp = AssignedIpDto(assignedIpUuid, issuerUuid, Random.nextLong(), LocalDateTime.now())
    }

    //IP 할당 해제 신청 취소 조건
    //IP_RELEASE_DEMAND_CANCEL 권한이 있어야하며, -> PolicyCheckService 에 위임
    //취소할 신청이 존재해야하며, -> PolicyCheckService 에 위임
    //요청자의 uuid가 신청자의 uuid와 같아야 한다. -> PolicyCheckService 에 위임
    @Test @DisplayName("IP 할당 해제 신청 취소 - 취소 성공")
    fun demandReleaseIpCancelSuccess() {
        val role = Role.values().filter { it.hasPermission(Permission.IP_RELEASE_DEMAND_CANCEL) }.random()
        val status = DemandStatusType.values().filter { it.isCancelable }.random()
        val dto = IpReleaseDemandDto(demandUuid, assignedIpUuid, issuerUuid, status)

        whenever(ipReleaseDemandQueryService.getDemandByUuid(any())).thenReturn(dto.toMono())
        whenever(ipReleaseDemandQueryService.existsDemandByUuid(any())).thenReturn(true.toMono())
        whenever(ipReleaseDemandService.cancel(demandUuid, issuerUuid)).thenReturn(Unit.toMono())

        val response = target.cancelDemandReleaseIp(demandUuid, issuerUuid, role).block()!!

        assert(response.statusCode.is2xxSuccessful)
        assert(response.body==Unit)
        verify(ipReleaseDemandService, times(1)).cancel(demandUuid, issuerUuid)
        verify(policyCheckService, times(1)).checkPermission(role, Permission.IP_RELEASE_DEMAND_CANCEL)
        verify(policyCheckService, times(1)).checkDemandExists(demandUuid)
        verify(policyCheckService, times(1)).checkDemandAccess(demandUuid, issuerUuid)
    }

    //IP 할당 해제 신청 조건
    //IP_RELEASE_DEMAND 권한이 있어야하며, -> PolicyCheckService 에 위임
    //동일한 assignedIp 로 진행중인 해제신청이 없어야 하며, --> Service 단에 위임
    //assignedIpUuid 가 실제 assignedIp 의 Uuid 여야하며, -> PolicyCheckService 에 위임
    //요청자의 uuid (issuerUuid)가 해당 assignedIp 의 소유자(issuer)의 uuid와 같아야한다. -> PolicyCheckService 에 위임
    @Test @DisplayName("IP 할당 해제 신청 - 신청 성공")
    fun demandReleaseIpSuccess() {
        val role = Role.values().filter { it.hasPermission(Permission.IP_RELEASE_DEMAND) }.random()
        val dto = IpReleaseDemandDto(demandUuid, assignedIpUuid, issuerUuid, DemandStatusType.CREATED)

        whenever(ipManageQueryService.existsAssignedIpByUuid(any())).thenReturn(true.toMono())
        whenever(ipManageQueryService.getAssignedIpByUuid(any())).thenReturn(assignedIp.toMono())
        whenever(ipReleaseDemandService.demand(assignedIpUuid, issuerUuid)).thenReturn(dto.toMono())
        whenever(eventPublishService.publish(Event.IP_RELEASE_DEMAND_ADD.routingKey, dto)).thenReturn(dto)

        val response = target.demandReleaseIp(assignedIpUuid, issuerUuid, role).block()!!
        val body = response.body!!

        assert(response.statusCode.is2xxSuccessful)
        assert(body.uuid == demandUuid)
        assert(body.assignedIpUuid == assignedIpUuid)
        assert(body.issuerUuid == issuerUuid)
        assert(body.status == DemandStatusType.CREATED)
        verify(ipReleaseDemandService, times(1)).demand(assignedIpUuid, issuerUuid)
        verify(eventPublishService, times(1)).publish(Event.IP_RELEASE_DEMAND_ADD.routingKey, dto)
        verify(policyCheckService, times(1)).checkPermission(role, Permission.IP_RELEASE_DEMAND)
        verify(policyCheckService, times(1)).checkAssignedIpExists(assignedIpUuid)
        verify(policyCheckService, times(1)).checkAssignedIpAccess(assignedIpUuid, issuerUuid)
    }
}