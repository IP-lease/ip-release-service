package com.iplease.server.ip.release.global.log.util

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ReserveSchedulerLoggerUtil: SimpleLoggerUtil<Unit, List<Unit>>(
    LoggerFactory.getLogger(ReserveSchedulerLoggerUtil::class.java),
    "[SCHEDULE] [해제예약 - 당일예약된 해제신청 등록]"
        ) {
    override fun logOnStart(input: Unit, uuid: String) {
        log("스케줄을 시작합니다.", uuid, true) { info(it) }
    }

    override fun logOnComplete(output: List<Unit>, uuid: String) {
        log("스케줄을 완료하였습니다.", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("스케줄 실행중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}