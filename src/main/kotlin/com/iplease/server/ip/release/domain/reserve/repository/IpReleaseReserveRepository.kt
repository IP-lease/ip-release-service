package com.iplease.server.ip.release.domain.reserve.repository

import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

interface IpReleaseReserveRepository: R2dbcRepository<IpReleaseReserveTable, Long> {
    fun existsByAssignedIpUuid(assignedIpUuid: Long): Mono<Boolean>
    fun findAllByReleaseAt(releaseAt: LocalDate): Flux<IpReleaseReserveTable>
}