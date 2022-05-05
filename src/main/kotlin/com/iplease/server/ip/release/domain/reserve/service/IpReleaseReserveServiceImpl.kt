package com.iplease.server.ip.release.domain.reserve.service

import com.iplease.server.ip.release.domain.reserve.data.dto.IpReleaseReserveDto
import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import com.iplease.server.ip.release.domain.reserve.exception.AlreadyReservedAssignedIpException
import com.iplease.server.ip.release.domain.reserve.exception.OutOfRangeReleaseDateException
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.common.repository.IpReleaseDemandRepository
import com.iplease.server.ip.release.global.common.util.DateUtil
import com.iplease.server.ip.release.global.demand.exception.AlreadyDemandedAssignedIpException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate

@Service
class IpReleaseReserveServiceImpl(
    private val ipReleaseDemandRepository: IpReleaseDemandRepository,
    private val ipReleaseReserveRepository: IpReleaseReserveRepository,
    private val dateUtil: DateUtil
): IpReleaseReserveService {
    override fun reserve(assignedIpUuid: Long, issuerUuid: Long, releaseAt: LocalDate) =
        checkAlreadyDemanded(assignedIpUuid)
            .flatMap { checkAlreadyReserved(assignedIpUuid) }
            .flatMap { checkReleaseDate(releaseAt) }
            .map{ IpReleaseReserveTable(0, assignedIpUuid, issuerUuid, releaseAt) }
            .flatMap { ipReleaseReserveRepository.save(it) }
            .map { IpReleaseReserveDto(it.uuid, it.assignedIpUuid, it.issuerUuid, it.releaseAt) }

    override fun cancelReserve(assignedIpUuid: Long): Mono<Unit> {
        TODO("Not yet implemented")
    }

    //TODO PolicyCheckService 를 통해 Controller 단에서 처리하는게 좋지 않을지 고려해보기
    private fun checkAlreadyDemanded(assignedIpUuid: Long): Mono<Unit> =
        ipReleaseDemandRepository.existsByAssignedIpUuid(assignedIpUuid).checkTemplate(AlreadyDemandedAssignedIpException(assignedIpUuid), true)

    private fun checkAlreadyReserved(assignedIpUuid: Long) =
        ipReleaseReserveRepository.existsByAssignedIpUuid(assignedIpUuid).checkTemplate(AlreadyReservedAssignedIpException(assignedIpUuid), true)

    private fun checkReleaseDate(releaseAt: LocalDate) =
        (dateUtil.dateNow() to dateUtil.dateNow().plusYears(1).withDayOfYear(1))
            .run { releaseAt.isAfter(first) && releaseAt.isBefore(second) }
            .toMono().checkTemplate(OutOfRangeReleaseDateException(releaseAt))

    private fun <T: RuntimeException> Mono<Boolean>.checkTemplate(onFailed : T, failedCondition: Boolean = false): Mono<Unit> =
        flatMap {
            if (it == failedCondition) Mono.error(onFailed)
            else Mono.just(Unit)
        }
}