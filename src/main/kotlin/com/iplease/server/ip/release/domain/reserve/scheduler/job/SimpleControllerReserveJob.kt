package com.iplease.server.ip.release.domain.reserve.scheduler.job

import com.iplease.server.ip.release.domain.demand.controller.IpReleaseDemandController
import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.global.common.util.DateUtil
import com.iplease.server.ip.release.global.demand.data.response.DemandReleaseIpResponse
import com.iplease.server.ip.release.global.log.service.LoggingService
import com.iplease.server.ip.release.global.log.type.LoggingActType
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class SimpleControllerReserveJob(
    val reserveRepository: IpReleaseReserveRepository,
    dateUtil: DateUtil,
    private val ipReleaseDemandController: IpReleaseDemandController,
    private val loggingService: LoggingService
) : SimpleReserveJob(reserveRepository, dateUtil) {
    override fun demand(table: IpReleaseReserveTable): Mono<IpReleaseDemandDto> =
        ipReleaseDemandController.demandReleaseIp(table.assignedIpUuid, table.issuerUuid, Role.USER)
            .map { responseToDto(it.body!!) }
            .let { loggingService.withLog(table, it, LoggingActType.RESERVE_JOB_DEMAND_LOGGER) }

    private fun responseToDto(it: DemandReleaseIpResponse) = IpReleaseDemandDto(it.uuid, it.assignedIpUuid, it.issuerUuid, it.status)

    override fun delete(table: IpReleaseReserveTable): Mono<Unit>  =
        reserveRepository.deleteById(table.uuid).then(Unit.toMono())
            .let { loggingService.withLog(table, it, LoggingActType.RESERVE_JOB_DELETE_LOGGER) }
}