package com.iplease.server.ip.release.domain.reserve.scheduler.job

import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.common.util.DateUtil
import reactor.kotlin.core.publisher.toMono

abstract class SimpleReserveJob(
    private val reserveRepository: IpReleaseReserveRepository,
    private val dateUtil: DateUtil
): ReserveJob {
    override fun reserveAtToday() =
        reserveRepository.findAllByReleaseAt(dateUtil.dateNow())
            .flatMap { reserve(it) }
            .retry()
            .onErrorContinue { _, _ -> }
            .flatMap { reserveRepository.deleteById(it.uuid).then(Unit.toMono()) }
}