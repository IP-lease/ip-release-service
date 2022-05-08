package com.iplease.server.ip.release.global.log.util

import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ReserveJobDeleteLoggerUtil: SimpleLoggerUtil<IpReleaseReserveTable, Unit>(
    LoggerFactory.getLogger(ReserveJobDeleteLoggerUtil::class.java),
    "[JOB] [해제예약 - 삭제]"
) {
    override fun logOnStart(input: IpReleaseReserveTable, uuid: String) {
        log("해제예약 정보를 토대로 예약삭제를 진행합니다.", uuid, true) { info(it) }
        log("해제예약 정보 : $input", uuid) { info(it) }
    }
    override fun logOnError(throwable: Throwable, uuid: String) {
        log("예약삭제중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }

    override fun logOnComplete(output: Unit, uuid: String) {
        log("예약삭제를 완료하였습니다.", uuid) { info(it) }
    }
}