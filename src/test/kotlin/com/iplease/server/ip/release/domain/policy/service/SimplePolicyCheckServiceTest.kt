package com.iplease.server.ip.release.domain.policy.service

import com.iplease.server.ip.release.domain.demand.data.dto.AssignedIpDto
import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.demand.data.type.DemandStatusType
import com.iplease.server.ip.release.domain.reserve.data.dto.IpReleaseReserveDto
import com.iplease.server.ip.release.global.demand.exception.UnknownAssignedIpException
import com.iplease.server.ip.release.global.demand.exception.WrongAccessAssignedIpException
import com.iplease.server.ip.release.global.demand.exception.WrongAccessDemandException
import com.iplease.server.ip.release.global.common.exception.PermissionDeniedException
import com.iplease.server.ip.release.global.common.data.type.Permission
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.global.demand.exception.UnknownDemandException
import com.iplease.server.ip.release.global.common.service.IpManageQueryService
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandQueryService
import com.iplease.server.ip.release.global.reserve.exception.UnknownReserveException
import com.iplease.server.ip.release.global.reserve.exception.WrongAccessReserveException
import com.iplease.server.ip.release.global.reserve.service.IpReleaseReserveQueryService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import kotlin.properties.Delegates
import kotlin.random.Random

class SimplePolicyCheckServiceTest {
    private lateinit var ipReleaseReserveQueryService: IpReleaseReserveQueryService
    private lateinit var ipReleaseDemandQueryService: IpReleaseDemandQueryService
    private lateinit var ipManageQueryService: IpManageQueryService
    private lateinit var target: SimplePolicyCheckService
    private var demandUuid by Delegates.notNull<Long>()
    private var reserveUuid by Delegates.notNull<Long>()
    private var assignedIpUuid by Delegates.notNull<Long>()
    private var operatorUuid by Delegates.notNull<Long>()

    @BeforeEach
    fun setUp() {
        ipReleaseDemandQueryService = mock()
        ipReleaseReserveQueryService = mock()
        ipManageQueryService = mock()

        target = SimplePolicyCheckService(
            ipReleaseDemandQueryService,
            ipReleaseReserveQueryService,
            ipManageQueryService
        )

        demandUuid = Random.nextLong()
        reserveUuid = Random.nextLong()
        assignedIpUuid = Random.nextLong()
        operatorUuid = Random.nextLong()
    }

    @Test @DisplayName("권한 검사 - 검사 성공")
    fun checkPermissionSuccess() {
        val permission = Permission.values().random()
        target.checkPermission(Role.ADMINISTRATOR, permission).block()
    }

    @Test @DisplayName("권한 검사 - 권한이 없을경우")
    fun checkPermissionFailure() {
        val permission = Permission.values().random()
        val exception = assertThrows<PermissionDeniedException> {
            target.checkPermission(Role.GUEST, permission).block()
        }
        assert(exception.permission == permission)
    }

    @Test @DisplayName("신청 존재 여부 검사 - 검사 성공")
    fun checkDemandExistsSuccess() {
        whenever(ipReleaseDemandQueryService.existsDemandByUuid(any())).thenReturn(false.toMono())

        val exception = assertThrows<UnknownDemandException> { target.checkDemandExists(demandUuid).block() }

        assert(exception.uuid == demandUuid)
    }

    @Test @DisplayName("신청 존재 여부 검사 - 신청이 존재하지 않을경우")
    fun checkDemandExistsFailure() {
        whenever(ipReleaseDemandQueryService.existsDemandByUuid(any())).thenReturn(true.toMono())
        target.checkDemandExists(demandUuid).block()
    }

    @Test @DisplayName("신청 접근 검사 - 검사 성공")
    fun checkDemandAccessSuccess() {
        val dto = IpReleaseDemandDto(demandUuid, assignedIpUuid, operatorUuid, DemandStatusType.CREATED)

        whenever(ipReleaseDemandQueryService.getDemandByUuid(any())).thenReturn(dto.toMono())

        target.checkDemandAccess(demandUuid, operatorUuid).block()
    }

    @Test @DisplayName("신청 접근 검사 - 접근이 불가능할 경우")
    fun checkDemandAccessFailure() {
        val dto = IpReleaseDemandDto(demandUuid, assignedIpUuid, operatorUuid * -1, DemandStatusType.CREATED)

        whenever(ipReleaseDemandQueryService.getDemandByUuid(any())).thenReturn(dto.toMono())

        val exception = assertThrows<WrongAccessDemandException> { target.checkDemandAccess(demandUuid, operatorUuid).block() }
        assert(exception.uuid == demandUuid)
        assert(exception.issuerUuid == operatorUuid)
    }

    @Test @DisplayName("할당IP 존재 여부 검사 - 검사 성공")
    fun assignedIpExistsSuccess() {
        whenever(ipManageQueryService.existsAssignedIpByUuid(any())).thenReturn(true.toMono())
        target.checkAssignedIpExists(assignedIpUuid).block()
    }

    @Test @DisplayName("할당IP 존재 여부 검사 - 할당IP가 존재하지 않을 경우")
    fun assignedIpExistsFailure() {
        whenever(ipManageQueryService.existsAssignedIpByUuid(any())).thenReturn(false.toMono())
        val exception = assertThrows<UnknownAssignedIpException> { target.checkAssignedIpExists(assignedIpUuid).block() }
        assert(exception.uuid == assignedIpUuid)
    }

    @Test @DisplayName("할당IP 접근 검사 - 검사 성공")
    fun assignedIpAccessSuccess() {
        val demanderUuid = operatorUuid
        val dto = AssignedIpDto(assignedIpUuid, demanderUuid, operatorUuid, LocalDate.now())
        whenever(ipManageQueryService.getAssignedIpByUuid(any())).thenReturn(dto.copy(issuerUuid = demanderUuid).toMono())

        target.checkAssignedIpAccess(assignedIpUuid, operatorUuid).block()
    }

    @Test @DisplayName("할당IP 접근 검사 - 접근이 불가능할 경우")
    fun assignedIpAccessFailure() {
        val demanderUuid = operatorUuid * -1
        val dto = AssignedIpDto(assignedIpUuid, demanderUuid, operatorUuid, LocalDate.now())
        whenever(ipManageQueryService.getAssignedIpByUuid(any())).thenReturn(dto.copy(issuerUuid = demanderUuid).toMono())

        val exception = assertThrows<WrongAccessAssignedIpException> { target.checkAssignedIpAccess(assignedIpUuid, operatorUuid).block() }

        assert(exception.uuid == assignedIpUuid)
        assert(exception.issuerUuid == operatorUuid)
    }

    @Test @DisplayName("예약 존재 여부 검사 - 검사 성공")
    fun checkReserveExistsSuccess() {
        whenever(ipReleaseReserveQueryService.existsReserveByUuid(any())).thenReturn(false.toMono())

        val exception = assertThrows<UnknownReserveException> { target.checkReserveExists(reserveUuid).block() }

        assert(exception.uuid == reserveUuid)
    }

    @Test @DisplayName("예약 존재 여부 검사 - 예약이 존재하지 않을경우")
    fun checkReserveExistsFailure() {
        whenever(ipReleaseReserveQueryService.existsReserveByUuid(any())).thenReturn(true.toMono())
        target.checkReserveExists(reserveUuid).block()
    }

    @Test @DisplayName("예약 접근 검사 - 검사 성공")
    fun checkReserveAccessSuccess() {
        val dto = IpReleaseReserveDto(reserveUuid, assignedIpUuid, operatorUuid, LocalDate.now())

        whenever(ipReleaseReserveQueryService.getReserveByUuid(any())).thenReturn(dto.toMono())

        target.checkReserveAccess(reserveUuid, operatorUuid).block()
    }

    @Test @DisplayName("예약 접근 검사 - 접근이 불가능할 경우")
    fun checkReserveAccessFailure() {
        val dto = IpReleaseReserveDto(reserveUuid, assignedIpUuid, operatorUuid * -1, LocalDate.now())

        whenever(ipReleaseReserveQueryService.getReserveByUuid(any())).thenReturn(dto.toMono())

        val exception = assertThrows<WrongAccessReserveException> { target.checkReserveAccess(reserveUuid, operatorUuid).block() }
        assert(exception.uuid == reserveUuid)
        assert(exception.issuerUuid == operatorUuid)
    }
}