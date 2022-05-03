package com.iplease.server.ip.release.domain.request.service

import com.iplease.server.ip.release.domain.request.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.request.exception.AlreadyDemandedAssignedIpException
import com.iplease.server.ip.release.domain.request.repository.IpReleaseDemandRepository
import com.iplease.server.ip.release.domain.request.data.table.IpReleaseDemandTable
import com.iplease.server.ip.release.domain.request.data.type.DemandStatusType
import com.iplease.server.ip.release.domain.request.exception.NotCancelableDemandException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class IpReleaseDemandServiceImpl(private val ipReleaseDemandRepository: IpReleaseDemandRepository) : IpReleaseDemandService {
    override fun demand(assignedIpUuid: Long, issuerUuid: Long): Mono<IpReleaseDemandDto> {
        val table = IpReleaseDemandTable(0L, assignedIpUuid, issuerUuid, DemandStatusType.CREATED)
        return ipReleaseDemandRepository.existsByAssignedIpUuid(assignedIpUuid)
            .flatMap {
                if(it) Mono.defer { Mono.error(AlreadyDemandedAssignedIpException(assignedIpUuid)) }
                else ipReleaseDemandRepository.save(table)
                    .map { table ->  IpReleaseDemandDto(table.uuid, table.assignedIpUuid, table.issuerUuid, table.status) }
            }
    }

    override fun cancel(uuid: Long, issuerUuid: Long): Mono<Unit> {
        return ipReleaseDemandRepository.findById(uuid)
            .flatMap {
                if(it.status.isCancelable)
                    ipReleaseDemandRepository.deleteById(uuid)
                        .map { }
                else
                    Mono.defer { Mono.error(NotCancelableDemandException(uuid)) }
            }
    }
}