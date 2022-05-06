package com.iplease.server.ip.release.domain.reserve.scheduler.job

import reactor.core.publisher.Flux

interface ReserveJob {
    fun reserveAtToday(): Flux<Unit>
}