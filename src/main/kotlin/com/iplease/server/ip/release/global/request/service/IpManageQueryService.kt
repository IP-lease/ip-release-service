package com.iplease.server.ip.release.global.request.service

import com.iplease.server.ip.release.domain.demand.data.dto.AssignedIpDto
import reactor.core.publisher.Mono

interface IpManageQueryService {
    fun existsAssignedIpByUuid(uuid: Mono<Long>): Mono<Boolean>
    fun getAssignedIpByUuid(uuid: Mono<Long>): Mono<AssignedIpDto>
}
