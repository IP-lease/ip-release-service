package com.iplease.server.ip.release.domain.reserve.scheduler.job

import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
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
    override fun reserve(table: IpReleaseReserveTable) = demandService.demand(table.assignedIpUuid, table.issuerUuid)
    override fun delete(first: IpReleaseDemandDto, second: IpReleaseReserveTable) =
        reserveRepository.deleteById(second.uuid).then(Unit.toMono())

}