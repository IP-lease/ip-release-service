package com.iplease.server.ip.release.global.common.service

import com.iplease.server.ip.release.domain.demand.data.dto.AssignedIpDto
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate

class DummyIpManageQueryService: IpManageQueryService {
    override fun existsAssignedIpByUuid(uuid: Mono<Long>) = true.toMono()
    override fun getAssignedIpByUuid(uuid: Mono<Long>) = uuid.map{ AssignedIpDto(it, 0L, 0L, LocalDate.MIN) }
}