package com.iplease.server.ip.release.domain.request.service

import com.iplease.server.ip.release.domain.request.data.dto.IpReleaseDemandDto
import reactor.core.publisher.Mono

interface IpReleaseDemandService {
    fun demand(assignedIpUuid: Long, issuerUuid: Long): Mono<IpReleaseDemandDto>
}
