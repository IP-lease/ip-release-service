package com.iplease.server.ip.release.domain.reserve.scheduler.job

import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ReserveJob {
    fun reserveAtToday(): Flux<Unit>
    fun reserve(table: IpReleaseReserveTable): Mono<IpReleaseDemandDto>
}