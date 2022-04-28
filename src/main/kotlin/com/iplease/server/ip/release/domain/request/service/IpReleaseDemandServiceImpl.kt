package com.iplease.server.ip.release.domain.request.service

import com.iplease.server.ip.release.domain.request.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.request.exception.AlreadyDemandedAssignedIpException
import com.iplease.server.ip.release.domain.request.repository.IpReleaseDemandRepository
import com.iplease.server.ip.release.domain.request.data.table.IpReleaseDemandTable
import com.iplease.server.ip.release.domain.request.data.type.DemandStatus
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class IpReleaseDemandServiceImpl(private val ipReleaseDemandRepository: IpReleaseDemandRepository) : IpReleaseDemandService {
    override fun demand(assignedIpUuid: Long, issuerUuid: Long): Mono<IpReleaseDemandDto> {
        val table = IpReleaseDemandTable(0L, assignedIpUuid, issuerUuid, DemandStatus.CREATED)
        return ipReleaseDemandRepository.existsByAssignedIpUuid(assignedIpUuid)
            .flatMap {
                if(it) Mono.defer { Mono.error(AlreadyDemandedAssignedIpException(assignedIpUuid)) }
                else ipReleaseDemandRepository.save(table)
                    .map { table ->  IpReleaseDemandDto(table.uuid, table.assignedIpUuid, table.issuerUuid, table.status) }
            }
    }

    override fun cancel(uuid: Long, issuerUuid: Long): Mono<Unit> {
        TODO("Not yet implemented")
    }
}