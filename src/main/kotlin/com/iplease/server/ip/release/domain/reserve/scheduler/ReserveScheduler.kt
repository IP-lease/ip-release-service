package com.iplease.server.ip.release.domain.reserve.scheduler

import com.iplease.server.ip.release.domain.reserve.scheduler.job.ReserveJob
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class ReserveScheduler(
    private val reserveJob: ReserveJob
) {
    val LOGGER = LoggerFactory.getLogger(this::class.java)
    val SCHEDULE_PREFIX = "[SCHEDULE]"
    @Scheduled(cron = "0 0 0,6,12,18 * * ?")
    fun executeReserve() {
        val PREFIX = "$SCHEDULE_PREFIX [해제예약 - 당일예약된 해제신청 등록]"
        LOGGER.info("$PREFIX 스케줄을 시작합니다.").toMono()
            .flatMap { reserveJob.reserveAtToday().collectList() }
            .subscribe{ LOGGER.info("$PREFIX 스케줄을 완료하였습니다.") }
    }
}