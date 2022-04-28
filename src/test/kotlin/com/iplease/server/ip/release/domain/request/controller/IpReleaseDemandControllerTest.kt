package com.iplease.server.ip.release.domain.request.controller

import com.iplease.server.ip.release.domain.request.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.global.grpc.service.IpManageQueryService
import com.iplease.server.ip.release.domain.request.service.IpReleaseDemandService
import com.iplease.server.ip.release.domain.request.data.type.DemandStatus
import com.iplease.server.ip.release.domain.request.exception.*
import com.iplease.server.ip.release.global.grpc.service.IpReleaseDemandQueryService
import com.iplease.server.ip.release.global.type.Permission
import com.iplease.server.ip.release.global.type.Role
import com.iplease.server.ip.release.global.grpc.dto.AssignedIp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleaseDemandControllerTest {
    private lateinit var target: IpReleaseDemandController
    private lateinit var ipReleaseDemandService: IpReleaseDemandService
    private lateinit var ipReleaseDemandQueryService: IpReleaseDemandQueryService
    private lateinit var ipManageQueryService: IpManageQueryService
    private var assignedIpUuid by Delegates.notNull<Long>()
    private var issuerUuid by Delegates.notNull<Long>()
    private lateinit var assignedIp: AssignedIp
    @BeforeEach
    fun setUp() {
        ipReleaseDemandService = mock()
        ipReleaseDemandQueryService = mock()
        ipManageQueryService = mock()
        target = IpReleaseDemandController(ipReleaseDemandService, ipReleaseDemandQueryService, ipManageQueryService)
        assignedIpUuid = Random.nextLong()
        issuerUuid = Random.nextLong()
        assignedIp = AssignedIp(assignedIpUuid, issuerUuid, Random.nextLong(), LocalDateTime.now())
    }

    //IP 할당 해제 신청 취소 조건
    //IP_RELEASE_DEMAND_CANCEL 권한이 있어야하며,
    //취소할 신청이 존재해야하며,
    //요청자의 uuid가 신청자의 uuid와 같아야 한다.
    @Test @DisplayName("IP 할당 해제 신청 취소 - 취소 성공")
    fun demandReleaseIpCancelSuccess() {
        val uuid = Random.nextLong()
        val dto = IpReleaseDemandDto(uuid, assignedIpUuid, issuerUuid, DemandStatus.CREATED)

        whenever(ipReleaseDemandQueryService.getDemandByUuid(uuid)).thenReturn(dto)
        whenever(ipReleaseDemandQueryService.existsDemandByUuid(uuid)).thenReturn(true)
        whenever(ipReleaseDemandService.cancel(uuid, issuerUuid)).thenReturn(Unit.toMono())

        val response = target.cancelDemandReleaseIp(uuid, issuerUuid, Role.ADMINISTRATOR).block()!!

        assert(response.statusCode.is2xxSuccessful)
        assert(response.body==Unit)
        verify(ipReleaseDemandService, times(1)).cancel(uuid, issuerUuid)
    }

    @Test @DisplayName("IP 할당 해제 신청 취소 - 권한이 없을경우")
    fun demandReleaseIpCancelFailurePermissionDenied() {
        val uuid = Random.nextLong()
        val dto = IpReleaseDemandDto(uuid, assignedIpUuid, issuerUuid * -1, DemandStatus.CREATED)

        whenever(ipReleaseDemandQueryService.existsDemandByUuid(uuid)).thenReturn(true)
        whenever(ipReleaseDemandQueryService.getDemandByUuid(uuid)).thenReturn(dto)

        val exception = assertThrows<PermissionDeniedException> { target.cancelDemandReleaseIp(uuid, issuerUuid, Role.GUEST).block() }

        assert(exception.permission == Permission.IP_RELEASE_DEMAND_CANCEL)
        verify(ipReleaseDemandService, times(0)).cancel(uuid, issuerUuid)
    }

    @Test @DisplayName("IP 할당 해제 신청 취소 - 신청이 존재하지 않을경우")
    fun demandReleaseIpCancelFailureDemandNotExists() {
        val uuid = Random.nextLong()

        whenever(ipReleaseDemandQueryService.existsDemandByUuid(uuid)).thenReturn(false)

        val exception = assertThrows<UnknownDemandException> { target.cancelDemandReleaseIp(uuid, issuerUuid, Role.ADMINISTRATOR).block() }

        assert(exception.uuid == uuid)
        verify(ipReleaseDemandService, times(0)).cancel(uuid, issuerUuid)
    }

    @Test @DisplayName("IP 할당 해제 신청 취소 - 요청자가 신청자가 아닐경우")
    fun demandReleaseIpCancelFailureNotOwner() {
        val uuid = Random.nextLong()
        val dto = IpReleaseDemandDto(uuid, assignedIpUuid, issuerUuid * -1, DemandStatus.CREATED)

        whenever(ipReleaseDemandQueryService.existsDemandByUuid(uuid)).thenReturn(true)
        whenever(ipReleaseDemandQueryService.getDemandByUuid(uuid)).thenReturn(dto)

        val exception = assertThrows<WrongAccessDemandException> { target.cancelDemandReleaseIp(uuid, issuerUuid, Role.ADMINISTRATOR).block() }

        assert(exception.uuid == uuid)
        assert(exception.issuerUuid == issuerUuid)
        verify(ipReleaseDemandService, times(0)).cancel(uuid, issuerUuid)
    }

    //IP 할당 해제 신청 조건
    //IP_RELEASE_DEMAND 권한이 있어야하며,
    //동일한 assignedIp 로 진행중인 해제신청이 없어야 하며, --> 서비스에서 검사 예정
    //assignedIpUuid 가 실제 assignedIp 의 Uuid 여야하며,
    //요청자의 uuid (issuerUuid)가 해당 assignedIp 의 소유자(issuer)의 uuid와 같아야한다.
    @Test @DisplayName("IP 할당 해제 신청 - 신청 성공")
    fun demandReleaseIpSuccess() {
        val uuid = Random.nextLong()
        val dto = IpReleaseDemandDto(uuid, assignedIpUuid, issuerUuid, DemandStatus.CREATED)

        whenever(ipManageQueryService.existsAssignedIpByUuid(assignedIpUuid)).thenReturn(true)
        whenever(ipManageQueryService.getAssignedIpByUuid(assignedIpUuid)).thenReturn(assignedIp)
        whenever(ipReleaseDemandService.demand(assignedIpUuid, issuerUuid)).thenReturn(dto.toMono())

        val response = target.demandReleaseIp(assignedIpUuid, issuerUuid, Role.ADMINISTRATOR).block()!!
        val body = response.body!!

        assert(response.statusCode.is2xxSuccessful)
        assert(body.uuid == uuid)
        assert(body.assignedIpUuid == assignedIpUuid)
        assert(body.issuerUuid == issuerUuid)
        assert(body.status == DemandStatus.CREATED)
        verify(ipReleaseDemandService, times(1)).demand(assignedIpUuid, issuerUuid)
    }

    @Test @DisplayName("IP 할당 해제 신청 - 권한이 없을경우")
    fun demandReleaseIpFailurePermissionDenied() {
        whenever(ipManageQueryService.existsAssignedIpByUuid(assignedIpUuid)).thenReturn(true)
        whenever(ipManageQueryService.getAssignedIpByUuid(assignedIpUuid)).thenReturn(assignedIp)

        val exception = assertThrows<PermissionDeniedException> { target.demandReleaseIp(assignedIpUuid, issuerUuid, Role.GUEST).block() }

        assert(exception.permission == Permission.IP_RELEASE_DEMAND)
        verify(ipReleaseDemandService, times(0)).demand(assignedIpUuid, issuerUuid)
    }

    @Test @DisplayName("IP 할당 해제 신청 - AssignedIp가 존재하지 않을 경우")
    fun demandReleaseIpFailureNotExists() {
        whenever(ipManageQueryService.existsAssignedIpByUuid(assignedIpUuid)).thenReturn(false)
        whenever(ipManageQueryService.getAssignedIpByUuid(assignedIpUuid)).thenReturn(assignedIp)

        val exception = assertThrows<UnknownAssignedIpException> { target.demandReleaseIp(assignedIpUuid, issuerUuid, Role.ADMINISTRATOR).block() }

        assert(exception.uuid == assignedIpUuid)
        verify(ipReleaseDemandService, times(0)).demand(assignedIpUuid, issuerUuid)
    }

    @Test @DisplayName("IP 할당 해제 신청 - 요청자가 AssignedIp 의 소유자가 아닐경우")
    fun demandReleaseIpFailureNotOwner() {
        whenever(ipManageQueryService.existsAssignedIpByUuid(assignedIpUuid)).thenReturn(true)
        whenever(ipManageQueryService.getAssignedIpByUuid(assignedIpUuid)).thenReturn(assignedIp.copy(issuerUuid = issuerUuid * -1))

        val exception = assertThrows<WrongAccessAssignedIpException> { target.demandReleaseIp(assignedIpUuid, issuerUuid, Role.ADMINISTRATOR).block() }

        assert(exception.uuid == assignedIpUuid)
        assert(exception.issuerUuid == issuerUuid)
        verify(ipReleaseDemandService, times(0)).demand(assignedIpUuid, issuerUuid)
    }
}