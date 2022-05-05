package com.iplease.server.ip.release.domain.reserve.repository

import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface IpReleaseReserveRepository: R2dbcRepository<IpReleaseReserveTable, Long> {
    fun existsByAssignedIpUuid(assignedIpUuid: Long): Mono<Boolean>
}