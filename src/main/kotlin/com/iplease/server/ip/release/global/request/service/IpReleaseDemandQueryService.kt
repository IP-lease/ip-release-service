package com.iplease.server.ip.release.global.request.service

import com.iplease.server.ip.release.domain.request.data.dto.IpReleaseDemandDto
import reactor.core.publisher.Mono

interface IpReleaseDemandQueryService {
    fun getDemandByUuid(uuid: Mono<Long>): Mono<IpReleaseDemandDto>
    fun existsDemandByUuid(uuid: Mono<Long>): Mono<Boolean>
}
