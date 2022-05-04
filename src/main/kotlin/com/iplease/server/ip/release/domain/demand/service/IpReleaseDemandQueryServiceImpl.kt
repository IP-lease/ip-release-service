package com.iplease.server.ip.release.domain.demand.service

import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.global.common.repository.IpReleaseDemandRepository
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandQueryService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class IpReleaseDemandQueryServiceImpl(
    val ipReleaseDemandRepository: IpReleaseDemandRepository
): IpReleaseDemandQueryService {
    override fun getDemandByUuid(uuid: Mono<Long>): Mono<IpReleaseDemandDto> =
        ipReleaseDemandRepository.findById(uuid)
            .map { IpReleaseDemandDto(it.uuid, it.assignedIpUuid, it.issuerUuid, it.status) }

    override fun existsDemandByUuid(uuid: Mono<Long>): Mono<Boolean> =
        ipReleaseDemandRepository.existsById(uuid)
}