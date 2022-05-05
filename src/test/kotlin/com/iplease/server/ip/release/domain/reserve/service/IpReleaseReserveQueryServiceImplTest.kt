package com.iplease.server.ip.release.domain.reserve.service

import com.iplease.server.ip.release.domain.reserve.data.dto.IpReleaseReserveDto
import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleaseReserveQueryServiceImplTest {
    private lateinit var repository: IpReleaseReserveRepository
    private lateinit var target: IpReleaseReserveQueryServiceImpl
    private var uuid by Delegates.notNull<Long>()

    @BeforeEach
    fun setUp() {
        repository = mock()
        target = IpReleaseReserveQueryServiceImpl(repository)
        uuid = Random.nextLong()
    }

    @Test @DisplayName("uuid로 해제 예약 조회")
    fun getDemandByUuid() {
        val result = IpReleaseReserveTable(
            Random.nextLong(),
            Random.nextLong(),
            Random.nextLong(),
            LocalDate.now()
        )
        whenever(repository.findById(any<Mono<Long>>())).thenReturn(result.toMono())
        val value = target.getReserveByUuid(uuid.toMono()).block()!!

        assert(value == result.toDto())
    }
    @Test @DisplayName("uuid로 해제 예약 존재 여부 확인")
    fun existsDemandByUuid() {
        val result = Random.nextBoolean()
        whenever(repository.existsById(any<Mono<Long>>())).thenReturn(result.toMono())
        val value = target.existsReserveByUuid(uuid.toMono()).block()!!

        assert(value == result)
    }
}

private fun IpReleaseReserveTable.toDto() = IpReleaseReserveDto(uuid, assignedIpUuid, issuerUuid, releaseAt)