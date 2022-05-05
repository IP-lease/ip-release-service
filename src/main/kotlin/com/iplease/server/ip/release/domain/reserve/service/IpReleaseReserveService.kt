package com.iplease.server.ip.release.domain.reserve.service

import com.iplease.server.ip.release.domain.reserve.dto.IpReleaseReserveDto
import reactor.core.publisher.Mono

interface IpReleaseReserveService {
    fun reserve(assignedIpUuid: Long, issuerUuid: Long): Mono<IpReleaseReserveDto>
}
