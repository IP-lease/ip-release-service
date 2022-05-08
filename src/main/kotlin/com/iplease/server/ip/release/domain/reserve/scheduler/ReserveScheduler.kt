package com.iplease.server.ip.release.domain.reserve.scheduler

import com.iplease.server.ip.release.domain.reserve.scheduler.job.ReserveJob
import com.iplease.server.ip.release.global.log.service.LoggingService
import com.iplease.server.ip.release.global.log.type.LoggingActType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ReserveScheduler(
    private val reserveJob: ReserveJob,
    private val loggingService: LoggingService
) {
    @Scheduled(cron = "0 0 0,6,12,18 * * ?")
    fun executeReserve() {
        reserveJob.reserveAtToday().collectList()
            .let { loggingService.withLog(Unit, it, LoggingActType.RESERVE_SCHEDULER_LOGGER) }
            .subscribe()
    }
}