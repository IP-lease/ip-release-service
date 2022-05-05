package com.iplease.server.ip.release.domain.reserve.controller

import com.iplease.server.ip.release.domain.reserve.dto.IpReleaseReserveDto
import com.iplease.server.ip.release.domain.reserve.service.IpReleaseReserveService
import com.iplease.server.ip.release.global.common.type.Permission
import com.iplease.server.ip.release.global.common.type.Role
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import kotlin.random.Random

class IpReleaseReserveControllerTest {
    private lateinit var ipReleaseReserveService: IpReleaseReserveService
    private lateinit var target: IpReleaseReserveController

    @BeforeEach
    fun setUp() {
        ipReleaseReserveService = mock()
        target = IpReleaseReserveController(ipReleaseReserveService)
    }
    //IP 할당 해제 예약 조건
    //IP_RELEASE_RESERVE 권한을 가지고 있어야하며
    //할당IP 가 존재해야 하며
    //할당IP의 소유자가 신청자여야 하며`
    //할당IP가 해제예약되지 않았어야 하며
    //예약일이 내일 - 현재 년도 마지막일 사이여하며
    //이미 에약이 존재하지 않아야한다. -> Service 단에 위임
    @Test
    fun reserveReleaseIpSuccess() {
        val reserveUuid = Random.nextLong()
        val assignedIpUuid = Random.nextLong()
        val operatorUuid = Random.nextLong()
        val releaseAt = LocalDate.now().plusDays(1)
        val dto = IpReleaseReserveDto(reserveUuid, assignedIpUuid, operatorUuid, releaseAt)
        val role = Role.values().filter { it.hasPermission(Permission.IP_RELEASE_RESERVE) }.random()
        whenever(ipReleaseReserveService.reserve(assignedIpUuid, operatorUuid)).thenReturn(dto.toMono())

        val response = target.reserveReleaseIp(assignedIpUuid, operatorUuid, role).block()!!
        val body = response.body!!

        assert(body.uuid == reserveUuid)
        assert(body.assignedIpUuid == assignedIpUuid)
        assert(body.issuerUuid == operatorUuid)
        assert(body.releaseAt == releaseAt)
        verify(ipReleaseReserveService, times(1)).reserve(assignedIpUuid, operatorUuid)
    }
}