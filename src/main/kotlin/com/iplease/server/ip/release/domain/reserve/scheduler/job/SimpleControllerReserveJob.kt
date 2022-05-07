package com.iplease.server.ip.release.domain.reserve.scheduler.job

import com.iplease.server.ip.release.domain.demand.controller.IpReleaseDemandController
import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.global.common.util.DateUtil
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class SimpleControllerReserveJob(
    val reserveRepository: IpReleaseReserveRepository,
    dateUtil: DateUtil,
    private val ipReleaseDemandController: IpReleaseDemandController
) : SimpleReserveJob(reserveRepository, dateUtil) {

    override fun reserve(table: IpReleaseReserveTable): Mono<IpReleaseDemandDto> =
        println("테스트")
            .let { ipReleaseDemandController.demandReleaseIp(table.assignedIpUuid, table.issuerUuid, Role.USER) }
            .map { it.body!! }
            .map { IpReleaseDemandDto(it.uuid, it.assignedIpUuid, it.issuerUuid, it.status) }

    override fun delete(first: IpReleaseDemandDto, second: IpReleaseReserveTable): Mono<Unit> {
        //println("삭제중입니다. \n신청정보 : ${first}\n예약정보 : ${second}")
        val result = reserveRepository.deleteById(second.uuid).then(Unit.toMono())
        //println("삭제를 완료하였습니다.")
        return result
    }
}