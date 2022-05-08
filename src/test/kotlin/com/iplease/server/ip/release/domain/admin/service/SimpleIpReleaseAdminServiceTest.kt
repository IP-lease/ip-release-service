package com.iplease.server.ip.release.domain.admin.service

import com.iplease.server.ip.release.domain.admin.exception.NotAcceptableDemandException
import com.iplease.server.ip.release.domain.demand.data.table.IpReleaseDemandTable
import com.iplease.server.ip.release.domain.demand.data.type.DemandStatusType
import com.iplease.server.ip.release.global.demand.repository.IpReleaseDemandRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.properties.Delegates
import kotlin.random.Random

class SimpleIpReleaseAdminServiceTest {
    private lateinit var target: SimpleIpReleaseAdminService
    private lateinit var repository: IpReleaseDemandRepository
    private var demandUuid by Delegates.notNull<Long>()
    private var operatorUuid by Delegates.notNull<Long>()
    private lateinit var status: DemandStatusType

    @BeforeEach
    fun setUp() {
        repository = mock()
        target = SimpleIpReleaseAdminService(repository)

        demandUuid = Random.nextLong()
        operatorUuid = Random.nextLong()
    }

    @Test @DisplayName("IP 할당 해제 수락 - 수락 성공")
    fun acceptDemandSuccess() {
        status = DemandStatusType.values().filter { it.isAcceptable }.random()
        val assignedIpUuid = Random.nextLong()
        val table = IpReleaseDemandTable(demandUuid, assignedIpUuid, operatorUuid, status)
        whenever(repository.findById(demandUuid)).thenReturn(table.toMono())
        whenever(repository.deleteById(any<Long>())).thenReturn(Mono.`when`(Mono.just(demandUuid)))

        val result = target.acceptDemand(demandUuid, operatorUuid).block()!!
        assert(result.demandUuid == demandUuid)
        assert(result.assignedIpUuid == assignedIpUuid)
        assert(result.operatorUuid == operatorUuid)
        verify(repository, times(1)).deleteById(demandUuid)
    }

    @Test @DisplayName("IP 할당 해제 수락 - 신청의 상태가 수락가능한(Acceptable) 상태가 아닐 경우")
    fun acceptDemandFailure() {
        status = DemandStatusType.values().filter { !it.isAcceptable }.random()
        val assignedIpUuid = Random.nextLong()
        val table = IpReleaseDemandTable(demandUuid, assignedIpUuid, operatorUuid, status)
        whenever(repository.findById(demandUuid)).thenReturn(table.toMono())

        val exception = assertThrows<NotAcceptableDemandException> { target.acceptDemand(demandUuid, operatorUuid).block()!! }
        verify(repository, times(0)).deleteById(demandUuid)
        assert(exception.uuid == demandUuid)
    }
}