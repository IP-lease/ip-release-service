package com.iplease.server.ip.release.global.request.service

import com.iplease.server.ip.release.domain.request.data.dto.AssignedIpDto
import reactor.core.publisher.Mono

//TODO gRPC 도입예정
interface IpManageQueryService {
    fun existsAssignedIpByUuid(uuid: Mono<Long>): Mono<Boolean>
    fun getAssignedIpByUuid(uuid: Mono<Long>): Mono<AssignedIpDto>
}
