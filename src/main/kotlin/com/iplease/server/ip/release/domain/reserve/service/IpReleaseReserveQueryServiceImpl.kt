package com.iplease.server.ip.release.domain.reserve.service

import com.iplease.server.ip.release.domain.reserve.data.dto.IpReleaseReserveDto
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.reserve.service.IpReleaseReserveQueryService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class IpReleaseReserveQueryServiceImpl(
    private val ipReleaseReserveRepository: IpReleaseReserveRepository
): IpReleaseReserveQueryService {
    override fun getReserveByUuid(uuid: Mono<Long>): Mono<IpReleaseReserveDto> =
        ipReleaseReserveRepository.findById(uuid)
            .map { IpReleaseReserveDto(it.uuid, it.assignedIpUuid, it.issuerUuid, it.releaseAt) }

    override fun existsReserveByUuid(uuid: Mono<Long>): Mono<Boolean> =
        ipReleaseReserveRepository.existsById(uuid)
}
