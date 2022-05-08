package com.iplease.server.ip.release.domain.reserve.scheduler.job

import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.common.util.DateUtil
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandService
import reactor.kotlin.core.publisher.toMono

class SimpleServiceReserveJob(
    private val reserveRepository: IpReleaseReserveRepository,
    dateUtil: DateUtil,
    private val demandService: IpReleaseDemandService
) : SimpleReserveJob(reserveRepository, dateUtil) {
    override fun demand(table: IpReleaseReserveTable) = demandService.demand(table.assignedIpUuid, table.issuerUuid)
    override fun delete(table: IpReleaseReserveTable) =
        reserveRepository.deleteById(table.uuid).then(Unit.toMono())

}