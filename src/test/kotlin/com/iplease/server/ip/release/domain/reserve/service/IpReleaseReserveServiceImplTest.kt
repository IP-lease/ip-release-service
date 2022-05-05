package com.iplease.server.ip.release.domain.reserve.service

import com.iplease.server.ip.release.global.demand.exception.AlreadyDemandedAssignedIpException
import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import com.iplease.server.ip.release.domain.reserve.exception.AlreadyReservedAssignedIpException
import com.iplease.server.ip.release.domain.reserve.exception.OutOfRangeReleaseDateException
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.common.repository.IpReleaseDemandRepository
import com.iplease.server.ip.release.global.common.util.DateUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate
import kotlin.properties.Delegates
import kotlin.random.Random

class IpReleaseReserveServiceImplTest {
    //TODO 다른 도메인의 repository 에 직접 접근하는것으로 인한 결합성 이슈 고려해보기(global repository 라도)
    private lateinit var ipReleaseDemandRepository: IpReleaseDemandRepository
    private lateinit var ipReleaseReserveRepository: IpReleaseReserveRepository
    private lateinit var dateUtil: DateUtil
    private lateinit var target: IpReleaseReserveServiceImpl
    private lateinit var reserveAt: LocalDate
    private lateinit var releaseAt: LocalDate
    private var assignedIpUuid by Delegates.notNull<Long>()
    private var operatorUuid by Delegates.notNull<Long>()
    private val monoJustFalse = false.toMono()

    @BeforeEach
    fun setUp() {
        reserveAt = LocalDate.now().withDayOfYear(1)
        releaseAt = reserveAt.plusDays((1L..365).random())
        assignedIpUuid = Random.nextLong()
        operatorUuid = Random.nextLong()

        ipReleaseReserveRepository = mock { on { existsByAssignedIpUuid(assignedIpUuid) } doReturn monoJustFalse }
        ipReleaseDemandRepository = mock { on { existsByAssignedIpUuid(assignedIpUuid) } doReturn monoJustFalse }
        dateUtil = mock { on { dateNow() }.thenReturn(reserveAt) }
        target = IpReleaseReserveServiceImpl(ipReleaseDemandRepository, ipReleaseReserveRepository, dateUtil)
    }

    //IP 할당 해제 예약 조건 (inService)
    //할당IP가 해제신청되지 않았어야 하며
    //이미 에약이 존재하지 않아야하며.
    //예약일이 내일 - 현재 년도 마지막일 사이여한다.
    @Test
    @DisplayName("IP 할당 해제 예약 - 예약 성공")
    fun reserveSuccess() {
        val table = IpReleaseReserveTable(0, assignedIpUuid, operatorUuid, releaseAt)
        val uuid = Random.nextLong()
        whenever(ipReleaseReserveRepository.save(any())).thenReturn(table.copy(uuid = uuid).toMono())

        val result = target.reserve(assignedIpUuid, operatorUuid, releaseAt).block()!!

        assert(result.uuid == uuid)
        assert(result.assignedIpUuid == assignedIpUuid)
        assert(result.issuerUuid == operatorUuid)
        assert(result.releaseAt == releaseAt)
        verify(ipReleaseReserveRepository, times(1)).save(table)
    }

    @Test
    @DisplayName("IP 할당 해제 예약 - 할당IP가 해제신청되었을 경우")
    fun reserveFailureAlreadyDemanded() {
        whenever(ipReleaseDemandRepository.existsByAssignedIpUuid(assignedIpUuid)).thenReturn(true.toMono())

        val exception = assertThrows<AlreadyDemandedAssignedIpException> { target.reserve(assignedIpUuid, operatorUuid, releaseAt).block()!! }

        assert(exception.assignedIpUuid == assignedIpUuid)
        verify(ipReleaseReserveRepository, times(0)).save(any())
    }

    @Test
    @DisplayName("IP 할당 해제 예약 - 이미 예약이 존재할 경우")
    fun reserveFailureAlreadyReserved() {
        whenever(ipReleaseReserveRepository.existsByAssignedIpUuid(assignedIpUuid)).thenReturn(true.toMono())

        val exception = assertThrows<AlreadyReservedAssignedIpException> { target.reserve(assignedIpUuid, operatorUuid, releaseAt).block()!! }

        assert(exception.assignedIpUuid == assignedIpUuid)
        verify(ipReleaseReserveRepository, times(0)).save(any())
    }

    @Test
    @DisplayName("IP 할당 해제 예약 - 예약일이 금년 말일 이후일 경우")
    fun reserveFailureNotThisYear() {
        releaseAt = reserveAt.plusYears((1L..10).random()) //임의로 테스트 년도를 10년 이내로 잡는다.
        whenever(dateUtil.dateNow()).thenReturn(reserveAt)

        val exception = assertThrows<OutOfRangeReleaseDateException> { target.reserve(assignedIpUuid, operatorUuid, releaseAt).block()!! }

        assert(exception.releaseDate == releaseAt)
        verify(ipReleaseReserveRepository, times(0)).save(any())
    }

    @Test
    @DisplayName("IP 할당 해제 예약 - 예약일이 오늘 또는 과거일 경우")
    fun reserveFailureTodayOrPast() {
        releaseAt = reserveAt.minusDays((0L..10).random()) //임의로 테스트 일자를 10일 이내로 잡는다.
        whenever(dateUtil.dateNow()).thenReturn(reserveAt)

        val exception = assertThrows<OutOfRangeReleaseDateException> { target.reserve(assignedIpUuid, operatorUuid, releaseAt).block()!! }

        assert(exception.releaseDate == releaseAt)
        verify(ipReleaseReserveRepository, times(0)).save(any())
    }
}