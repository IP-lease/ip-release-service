package com.iplease.server.ip.release.domain.request.service

import com.iplease.server.ip.release.domain.request.exception.AlreadyDemandedAssignedIpException
import com.iplease.server.ip.release.domain.request.repository.IpReleaseDemandRepository
import com.iplease.server.ip.release.domain.request.data.table.IpReleaseDemandTable
import com.iplease.server.ip.release.domain.request.data.type.DemandStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.kotlin.core.publisher.toMono
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleaseDemandServiceImplTest {
    lateinit var repository: IpReleaseDemandRepository
    lateinit var target: IpReleaseDemandServiceImpl
    var assignedIpUuid by Delegates.notNull<Long>()
    var issuerUuid by Delegates.notNull<Long>()

    @BeforeEach
    fun setUp() {
        repository = mock()
        target = IpReleaseDemandServiceImpl(repository)
        assignedIpUuid = Random.nextLong()
        issuerUuid = Random.nextLong()
    }

    //IP 할당 해제 신청 조건
    //동일한 assignedIp 로 진행중인 해제신청이 없어야 한다.
    @Test @DisplayName("IP 할당 해제 신청 - 신청 성공")
    fun demandReleaseIpSuccess() {
        val uuid = Random.nextLong()
        val table = IpReleaseDemandTable(0L, assignedIpUuid, issuerUuid, DemandStatus.CREATED)
        whenever(repository.save(table)).thenReturn(table.copy(uuid = uuid).toMono())
        whenever(repository.existsByAssignedIpUuid(assignedIpUuid)).thenReturn(false.toMono())

        val dto = target.demand(assignedIpUuid, issuerUuid).block()!!

        assert(dto.uuid == uuid)
        assert(dto.assignedIpUuid == assignedIpUuid)
        assert(dto.issuerUuid == issuerUuid)

        verify(repository, times(1)).save(table)
    }

    @Test @DisplayName("IP 할당 해제 신청 - 이미 해제신청이 진행중일 경우")
    fun demandReleaseIpFailureAlreadyDemanded() {
        val uuid = Random.nextLong()
        val table = IpReleaseDemandTable(uuid, assignedIpUuid, issuerUuid, DemandStatus.CREATED)

        whenever(repository.existsByAssignedIpUuid(assignedIpUuid)).thenReturn(true.toMono())

        val exception = assertThrows<AlreadyDemandedAssignedIpException> {
            target.demand(assignedIpUuid, issuerUuid).block()
        }
        assert(exception.assignedIpUuid == assignedIpUuid)

        verify(repository, times(0)).save(table)
    }
}