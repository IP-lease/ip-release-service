package com.iplease.server.ip.release.domain.reserve.scheduler.job

import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.common.util.DateUtil
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandService
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class ReserveJobImpl(
    private val reserveRepository: IpReleaseReserveRepository,
    private val dateUtil: DateUtil,
    private val demandService: IpReleaseDemandService
) : ReserveJob {
    override fun reserveAtToday() =
        reserveRepository.findAllByReleaseAt(dateUtil.dateNow())
            .flatMap { demandService.demand(it.assignedIpUuid, it.issuerUuid) }
            .retry()
            .onErrorContinue { _, _ ->  }
            .flatMap { reserveRepository.deleteById(it.uuid).then(Unit.toMono()) }
}