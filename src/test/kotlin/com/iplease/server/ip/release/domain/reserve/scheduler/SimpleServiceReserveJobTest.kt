package com.iplease.server.ip.release.domain.reserve.scheduler

import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.demand.data.type.DemandStatusType
import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandService
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.domain.reserve.scheduler.job.SimpleServiceReserveJob
import com.iplease.server.ip.release.global.common.util.DateUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import kotlin.random.Random

class SimpleServiceReserveJobTest {
    private lateinit var reserveRepository: IpReleaseReserveRepository
    private lateinit var dateUtil: DateUtil
    private lateinit var demandService: IpReleaseDemandService
    private lateinit var target: SimpleServiceReserveJob
    private lateinit var releaseAt: LocalDate

    @BeforeEach
    fun setUp() {
        val year = (1970..2080).random()
        val month = (1 .. 12).random()
        val day = (1 .. 28).random()
        releaseAt = LocalDate.of(year, month, day)

        reserveRepository = mock()
        dateUtil = mock()
        demandService = mock()
        target = SimpleServiceReserveJob(reserveRepository, dateUtil, demandService)
    }

    //reserveAtToday 실행 로직
    //releaseAt 이 오늘 날짜인 모든 예약을 불러온다
    //불러온 예약을 전부 신청시킨다.
    //신청이 완료되면 해당 예약들을 제거한다.
    //만약 신청도중 특정 신청이 실패할 경우, 해당 신청을 제외하고 예약들을 제거한다.
    @Test @DisplayName("금일 예약 실행 - 실행 성공")
    fun reserveAtTodaySuccess() {
        val range = (1..Random.nextLong(99L)+1)
        val count = range.count()
        val flux = range
            .map { randomReserveTable() }
            .toFlux()

        whenever(dateUtil.dateNow()).thenReturn(releaseAt)
        whenever(demandService.demand(any(), any())).thenReturn(randomDemandDto().toMono())
        whenever(reserveRepository.findAllByReleaseAt(releaseAt)).thenReturn(flux)
        whenever(reserveRepository.deleteById(any<Long>())).thenReturn(Mono.just("").then())

        target.reserveAtToday().subscribe()

        verify(reserveRepository).findAllByReleaseAt(argThat{ isEqual(releaseAt) })
        verify(demandService, times(count)).demand(any(), any())
        verify(reserveRepository, times(count)).deleteById(any<Long>())
    }

    @Test @DisplayName("금일 예약 실행 - 특정 신청이 실패할 경우")
    fun reserveAtTodayFailureDemandFailed() {
        var count = 0
        val errorTables = mutableListOf<IpReleaseReserveTable>()
        val completeTables = mutableListOf<IpReleaseReserveTable>()
        val flux = (1..100)
            .map { randomReserveTable() }
            .toFlux()
            .flatMap {
                if(!listOf(true, true, true, true, true, true, false).random()) {
                    println(it.uuid.toString() + "은 오류를 발생시킵니다. - count: $count")
                    errorTables.add(it)
                    Mono.defer { throw RuntimeException() }
                } else {
                    count++
                    println(it.uuid.toString() + "은 오류를 발생시키지 않습니다. - count: $count")
                    completeTables.add(it)
                    it.toMono()
                }
            }

        whenever(dateUtil.dateNow()).thenReturn(releaseAt)
        whenever(demandService.demand(any(), any())).thenReturn(randomDemandDto().toMono())
        whenever(reserveRepository.findAllByReleaseAt(releaseAt)).thenReturn(flux)
        whenever(reserveRepository.deleteById(any<Long>()))
            .then{
                return@then println(it.arguments[0].toString() + "이 삭제되었습니다.")
                    .let{Mono.just("")}
                    .then()
            }

        target.reserveAtToday().subscribe()

        verify(reserveRepository).findAllByReleaseAt(argThat{ isEqual(releaseAt) })
        verify(demandService, times(count)).demand(any(), any())
        verify(reserveRepository, times(count)).deleteById(any<Long>())

        println("테스트 종료")
    }


    private fun randomReserveTable() = IpReleaseReserveTable(Random.nextLong(), Random.nextLong(), Random.nextLong(), releaseAt)
    private fun randomDemandDto() = IpReleaseDemandDto(Random.nextLong(), Random.nextLong(), Random.nextLong(), DemandStatusType.CREATED)
}