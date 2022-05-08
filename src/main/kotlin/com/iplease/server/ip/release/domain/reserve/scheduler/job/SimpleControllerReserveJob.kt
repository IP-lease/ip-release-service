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
import org.slf4j.LoggerFactory
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
    val LOGGER = LoggerFactory.getLogger(this::class.java)
    val JOB_PREFIX = "[JOB]"
    override fun demand(table: IpReleaseReserveTable): Mono<IpReleaseDemandDto> =
        ipReleaseDemandController.demandReleaseIp(table.assignedIpUuid, table.issuerUuid, Role.USER)
            .map { responseToDto(it.body!!) }
            .let { loggingService.withLog(table, it, LoggingActType.DEMAND_JOB_LOGGER) }

    private fun responseToDto(it: DemandReleaseIpResponse) = IpReleaseDemandDto(it.uuid, it.assignedIpUuid, it.issuerUuid, it.status)

    override fun delete(table: IpReleaseReserveTable): Mono<Unit>  = logDeleteStart(table)
        .flatMap { reserveRepository.deleteById(table.uuid).then(Unit.toMono()) }
        .map { logDeleteComplete() }

    private fun logDeleteStart(table: IpReleaseReserveTable): Mono<Unit> {
        val PREFIX = "$JOB_PREFIX [해제예약 - 삭제]"
        LOGGER.info("$PREFIX 해제예약 정보를 토대로 예약삭제를 진행합니다.")
        LOGGER.info("$PREFIX     해제예약 정보 : $table")
        return Unit.toMono()
    }

    private fun logDeleteComplete() {
        val PREFIX = "$JOB_PREFIX [해제예약 - 삭제]"
        LOGGER.info("$PREFIX     예약삭제를 완료하였습니다.")
    }
}