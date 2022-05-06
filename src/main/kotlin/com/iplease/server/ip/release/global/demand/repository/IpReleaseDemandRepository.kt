package com.iplease.server.ip.release.global.demand.repository

import com.iplease.server.ip.release.domain.demand.data.table.IpReleaseDemandTable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface IpReleaseDemandRepository: R2dbcRepository<IpReleaseDemandTable, Long> {
    fun existsByAssignedIpUuid(assignedIpUuid: Long): Mono<Boolean>
}