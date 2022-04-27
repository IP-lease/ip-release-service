package com.iplease.server.ip.release.domain.request.controller

import com.iplease.server.ip.release.domain.request.exception.PermissionDeniedException
import com.iplease.server.ip.release.domain.request.exception.UnknownAssignedIpException
import com.iplease.server.ip.release.domain.request.exception.WrongAccessAssignedIpException
import com.iplease.server.ip.release.global.grpc.service.IpManageService
import com.iplease.server.ip.release.domain.request.service.IpReleaseDemandService
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
import java.time.LocalDateTime
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleaseDemandControllerTest {
    lateinit var target: IpReleaseDemandController
    lateinit var ipReleaseDemandService: IpReleaseDemandService
    lateinit var ipManageService: IpManageService
    var assignedIpUuid by Delegates.notNull<Long>()
    var issuerUuid by Delegates.notNull<Long>()
    lateinit var assignedIp: AssignedIp
    @BeforeEach
    fun setUp() {
        ipReleaseDemandService = mock()
        ipManageService = mock()
        target = IpReleaseDemandController(ipReleaseDemandService, ipManageService)
        assignedIpUuid = Random.nextLong()
        issuerUuid = Random.nextLong()
        assignedIp = AssignedIp(assignedIpUuid, issuerUuid, Random.nextLong(), LocalDateTime.now())
    }

    //IP 할당 해제 신청 조건
    //IP_RELEASE_DEMAND 권한이 있어야하며,
    //동일한 assignedIp 로 진행중인 해제신청이 없어야 하며, --> 서비스에서 검사 예정
    //assignedIpUuid 가 실제 assignedIp 의 Uuid 여야하며,
    //요청자의 uuid (issuerUuid)가 해당 assignedIp 의 소유자(issuer)의 uuid와 같아야한다.
    @Test @DisplayName("IP 할당 해제 신청 - 신청 성공")
    fun demandReleaseIpSuccess() {
        whenever(ipManageService.existsAssignedIpByUuid(assignedIpUuid)).thenReturn(true)
        whenever(ipManageService.getAssignedIpByUuid(assignedIpUuid)).thenReturn(assignedIp)

        target.demandReleaseIp(assignedIpUuid, issuerUuid, Role.ADMINISTRATOR)

        verify(ipReleaseDemandService, times(1)).demand(assignedIpUuid, issuerUuid)
    }

    @Test @DisplayName("IP 할당 해제 신청 - 권한이 없을경우")
    fun demandReleaseIpFailurePermissionDenied() {
        whenever(ipManageService.existsAssignedIpByUuid(assignedIpUuid)).thenReturn(true)
        whenever(ipManageService.getAssignedIpByUuid(assignedIpUuid)).thenReturn(assignedIp)

        val exception = assertThrows<PermissionDeniedException> { target.demandReleaseIp(assignedIpUuid, issuerUuid, Role.GUEST) }
        assert(exception.permission == Permission.IP_RELEASE_DEMAND)

        verify(ipReleaseDemandService, times(0)).demand(assignedIpUuid, issuerUuid)
    }

    @Test @DisplayName("IP 할당 해제 신청 - AssignedIp가 존재하지 않을 경우")
    fun demandReleaseIpFailureNotExists() {
        whenever(ipManageService.existsAssignedIpByUuid(assignedIpUuid)).thenReturn(false)
        whenever(ipManageService.getAssignedIpByUuid(assignedIpUuid)).thenReturn(assignedIp)

        val exception = assertThrows<UnknownAssignedIpException> { target.demandReleaseIp(assignedIpUuid, issuerUuid, Role.ADMINISTRATOR) }
        assert(exception.uuid == assignedIpUuid)

        verify(ipReleaseDemandService, times(0)).demand(assignedIpUuid, issuerUuid)
    }

    @Test @DisplayName("IP 할당 해제 신청 - 요청자가 AssignedIp 의 소유자가 아닐경우")
    fun demandReleaseIpFailureNotOwner() {
        whenever(ipManageService.existsAssignedIpByUuid(assignedIpUuid)).thenReturn(true)
        whenever(ipManageService.getAssignedIpByUuid(assignedIpUuid)).thenReturn(assignedIp.copy(issuerUuid = issuerUuid * -1))

        val exception = assertThrows<WrongAccessAssignedIpException> { target.demandReleaseIp(assignedIpUuid, issuerUuid, Role.ADMINISTRATOR) }
        assert(exception.uuid == assignedIpUuid)
        assert(exception.issuerUuid == issuerUuid)

        verify(ipReleaseDemandService, times(0)).demand(assignedIpUuid, issuerUuid)
    }
}