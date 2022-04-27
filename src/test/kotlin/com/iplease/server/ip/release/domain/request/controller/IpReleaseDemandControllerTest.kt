package com.iplease.server.ip.release.domain.request.controller

import com.iplease.server.ip.release.global.grpc.service.IpManageService
import com.iplease.server.ip.release.domain.request.service.IpReleaseDemandService
import com.iplease.server.ip.release.global.Role
import com.iplease.server.ip.release.global.grpc.dto.AssignedIp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import kotlin.random.Random

class IpReleaseDemandControllerTest {
    lateinit var target: IpReleaseDemandController
    lateinit var ipReleaseDemandService: IpReleaseDemandService
    lateinit var ipManageService: IpManageService
    @BeforeEach
    fun setUp() {
        ipReleaseDemandService = mock()
        ipManageService = mock()
        target = IpReleaseDemandController(ipReleaseDemandService, ipManageService)
    }

    @Test @DisplayName("IP 할당 해제 신청 - 신청 성공")
    fun demandReleaseIpSuccess() {
        //assignedIpUuid 가 실제 assignedIp 의 Uuid 여야하며,
        //요청자의 uuid (issuerUuid)가 해당 assignedIp 의 소유자(issuer)의 uuid와 같아야한다.
        val assignedIpUuid = Random.nextLong()
        val issuerUuid = Random.nextLong()
        val assignedIp = AssignedIp(assignedIpUuid, issuerUuid, Random.nextLong(), LocalDateTime.now())

        whenever(ipManageService.existsAssignedIpByUuid(assignedIpUuid)).thenReturn(true)
        whenever(ipManageService.getAssignedIpByUuid(assignedIpUuid)).thenReturn(assignedIp)

        target.demandReleaseIp(assignedIpUuid, issuerUuid, Role.ADMINISTRATOR)

        verify(ipReleaseDemandService, times(1)).demand(assignedIpUuid, issuerUuid)
    }
}