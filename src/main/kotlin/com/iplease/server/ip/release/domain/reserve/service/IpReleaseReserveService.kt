package com.iplease.server.ip.release.domain.reserve.service

import com.iplease.server.ip.release.domain.reserve.data.dto.IpReleaseReserveDto
import reactor.core.publisher.Mono

interface IpReleaseReserveService {
    fun reserve(assignedIpUuid: Long, issuerUuid: Long): Mono<IpReleaseReserveDto>
}
