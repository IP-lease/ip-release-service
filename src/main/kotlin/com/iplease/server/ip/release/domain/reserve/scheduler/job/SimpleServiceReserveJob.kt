package com.iplease.server.ip.release.domain.reserve.scheduler.job

import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.common.util.DateUtil
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandService

class SimpleServiceReserveJob(
    reserveRepository: IpReleaseReserveRepository,
    dateUtil: DateUtil,
    private val demandService: IpReleaseDemandService
) : SimpleReserveJob(reserveRepository, dateUtil) {
    override fun reserve(table: IpReleaseReserveTable) = demandService.demand(table.assignedIpUuid, table.issuerUuid)
}