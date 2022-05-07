package com.iplease.server.ip.release.domain.reserve.scheduler

import com.iplease.server.ip.release.domain.reserve.scheduler.job.ReserveJob
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ReserveScheduler(
    private val reserveJob: ReserveJob
) {
    @Scheduled(fixedDelay = 10000)
    fun executeReserve() {
        reserveJob.reserveAtToday().subscribe()
    }
}