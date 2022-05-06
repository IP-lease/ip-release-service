package com.iplease.server.ip.release.domain.admin.service

import com.iplease.server.ip.release.domain.admin.data.dto.IpReleaseAcceptDto
import com.iplease.server.ip.release.domain.admin.exception.NotAcceptableDemandException
import com.iplease.server.ip.release.global.demand.repository.IpReleaseDemandRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class SimpleIpReleaseAdminService(
    val ipReleaseDemandRepository: IpReleaseDemandRepository
) : IpReleaseAdminService {
    override fun acceptDemand(demandUuid: Long, operatorUuid: Long) =
        ipReleaseDemandRepository.findById(demandUuid)
            .flatMap {
                if(it.status.isAcceptable) acceptDemandComplete(demandUuid, operatorUuid)
                else Mono.error(NotAcceptableDemandException(demandUuid))
            }.then(IpReleaseAcceptDto(demandUuid, operatorUuid).toMono())

    protected fun acceptDemandComplete(demandUuid: Long, operatorUuid: Long) =
        ipReleaseDemandRepository.deleteById(demandUuid)
}