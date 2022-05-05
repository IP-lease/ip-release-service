package com.iplease.server.ip.release.global.reserve.service

import com.iplease.server.ip.release.domain.reserve.data.dto.IpReleaseReserveDto
import reactor.core.publisher.Mono

interface IpReleaseReserveQueryService {
    fun getReserveByUuid(uuid: Mono<Long>): Mono<IpReleaseReserveDto>
    fun existsReserveByUuid(uuid: Mono<Long>): Mono<Boolean>
}
