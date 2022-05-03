package com.iplease.server.ip.release.domain.request.service

import com.iplease.server.ip.release.domain.request.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.request.data.table.IpReleaseDemandTable
import com.iplease.server.ip.release.domain.request.data.type.DemandStatusType
import com.iplease.server.ip.release.domain.request.repository.IpReleaseDemandRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleaseDemandQueryServiceImplTest {
    private lateinit var repository: IpReleaseDemandRepository
    private lateinit var target: IpReleaseDemandQueryServiceImpl
    private var uuid by Delegates.notNull<Long>()

    @BeforeEach
    fun setUp() {
        repository = mock()
        target = IpReleaseDemandQueryServiceImpl(repository)
        uuid = Random.nextLong()
    }

    @Test @DisplayName("uuid로 해제 신청 조회")
    fun getDemandByUuid() {
        val result = IpReleaseDemandTable(
            Random.nextLong(),
            Random.nextLong(),
            Random.nextLong(),
            DemandStatusType.values().random()
        )
        whenever(repository.findById(any<Mono<Long>>())).thenReturn(result.toMono())
        val value = target.getDemandByUuid(uuid.toMono()).block()!!

        assert(value == result.toDto())
    }
    @Test @DisplayName("uuid로 해제 신청 존재 여부 확인")
    fun existsDemandByUuid() {
        val result = Random.nextBoolean()
        whenever(repository.existsById(any<Mono<Long>>())).thenReturn(result.toMono())
        val value = target.existsDemandByUuid(uuid.toMono()).block()!!

        assert(value == result)
    }
}

private fun IpReleaseDemandTable.toDto() =
    IpReleaseDemandDto(
        uuid,
        assignedIpUuid,
        issuerUuid,
        status
    )
