package com.iplease.server.ip.release.domain.admin.service

import com.iplease.server.ip.release.domain.admin.dto.IpReleaseAcceptDto
import reactor.core.publisher.Mono

interface IpReleaseAdminService {
    fun acceptDemand(demandUuid: Long, operatorUuid: Long): Mono<IpReleaseAcceptDto>
}
