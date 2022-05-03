package com.iplease.server.ip.release.domain.demand.service

import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
import reactor.core.publisher.Mono

interface IpReleaseDemandService {
    fun demand(assignedIpUuid: Long, issuerUuid: Long): Mono<IpReleaseDemandDto>
    fun cancel(uuid: Long, issuerUuid: Long): Mono<Unit>
}
