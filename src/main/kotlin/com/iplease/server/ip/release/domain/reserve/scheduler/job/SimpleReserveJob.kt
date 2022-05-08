package com.iplease.server.ip.release.domain.reserve.scheduler.job

import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.common.util.DateUtil
import reactor.core.publisher.Mono

abstract class SimpleReserveJob(
    private val reserveRepository: IpReleaseReserveRepository,
    private val dateUtil: DateUtil
): ReserveJob {
    override fun reserveAtToday() =
        reserveRepository.findAllByReleaseAt(dateUtil.dateNow())
            .flatMap { demand(it).map { _ -> it } }
            .onErrorContinue { _, _ ->  }
            .flatMap { delete(it) }

    abstract fun demand(table: IpReleaseReserveTable): Mono<IpReleaseDemandDto>
    abstract fun delete(table: IpReleaseReserveTable): Mono<Unit>
}