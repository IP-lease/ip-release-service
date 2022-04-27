package com.iplease.server.ip.release.domain.request.service

import com.iplease.server.ip.release.domain.request.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.request.repository.IpReleaseDemandRepository
import com.iplease.server.ip.release.domain.request.table.IpReleaseDemandTable
import com.iplease.server.ip.release.domain.request.type.DemandStatus
import reactor.core.publisher.Mono

class IpReleaseDemandServiceImpl(val repository: IpReleaseDemandRepository) : IpReleaseDemandService {
    override fun demand(assignedIpUuid: Long, issuerUuid: Long): Mono<IpReleaseDemandDto> {
        val table = IpReleaseDemandTable(0L, assignedIpUuid, issuerUuid, DemandStatus.CREATED)
        return repository.save(table)
            .map { IpReleaseDemandDto(it.uuid, it.assignedIpUuid, it.issuerUuid, it.status) }
    }
}