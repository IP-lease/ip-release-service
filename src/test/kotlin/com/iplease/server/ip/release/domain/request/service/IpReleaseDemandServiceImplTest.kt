package com.iplease.server.ip.release.domain.request.service

import com.iplease.server.ip.release.domain.request.exception.AlreadyDemandedAssignedIpException
import com.iplease.server.ip.release.domain.request.repository.IpReleaseDemandRepository
import com.iplease.server.ip.release.domain.request.data.table.IpReleaseDemandTable
import com.iplease.server.ip.release.domain.request.data.type.DemandStatusType
import com.iplease.server.ip.release.domain.request.exception.NotCancelableDemandException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleaseDemandServiceImplTest {
    private lateinit var repository: IpReleaseDemandRepository
    private lateinit var target: IpReleaseDemandServiceImpl
    private var assignedIpUuid by Delegates.notNull<Long>()
    private var issuerUuid by Delegates.notNull<Long>()
    private var uuid by Delegates.notNull<Long>()

    @BeforeEach
    fun setUp() {
        repository = mock()
        target = IpReleaseDemandServiceImpl(repository)
        assignedIpUuid = Random.nextLong()
        issuerUuid = Random.nextLong()
        uuid = Random.nextLong()
    }

    //IP 할당 해제 신청 취소 조건
    //해당 신청이 취소 가능한 상태여야한다.
    @Test @DisplayName("IP 할당 해제 신청 취소 - 취소 성공")
    fun demandReleaseIpCancelSuccess() {
        val table = IpReleaseDemandTable(uuid, assignedIpUuid, issuerUuid, DemandStatusType.CREATED)

        whenever(repository.findById(uuid)).thenReturn(table.toMono())
        whenever(repository.deleteById(uuid)).thenReturn(Mono.just("").then())

        target.cancel(uuid, issuerUuid).block()

        verify(repository, times(1)).deleteById(uuid)
    }

    @Test @DisplayName("IP 할당 해제 신청 취소 - 취소할 수 없는 신청일 경우")
    fun demandReleaseIpCancelFailureAlreadyCompleted() {
        val table = IpReleaseDemandTable(uuid, assignedIpUuid, issuerUuid, DemandStatusType.COMPLETE)

        whenever(repository.findById(uuid)).thenReturn(table.toMono())

        val exception = assertThrows<NotCancelableDemandException> { target.cancel(uuid, issuerUuid).block() }

        assert(exception.uuid == uuid)
        verify(repository, times(0)).deleteById(uuid)
    }

    //IP 할당 해제 신청 조건
    //동일한 assignedIp 로 진행중인 해제신청이 없어야 한다.
    @Test @DisplayName("IP 할당 해제 신청 - 신청 성공")
    fun demandReleaseIpSuccess() {
        val table = IpReleaseDemandTable(0L, assignedIpUuid, issuerUuid, DemandStatusType.CREATED)
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
        val table = IpReleaseDemandTable(uuid, assignedIpUuid, issuerUuid, DemandStatusType.CREATED)

        whenever(repository.existsByAssignedIpUuid(assignedIpUuid)).thenReturn(true.toMono())

        val exception = assertThrows<AlreadyDemandedAssignedIpException> {
            target.demand(assignedIpUuid, issuerUuid).block()
        }
        assert(exception.assignedIpUuid == assignedIpUuid)

        verify(repository, times(0)).save(table)
    }
}