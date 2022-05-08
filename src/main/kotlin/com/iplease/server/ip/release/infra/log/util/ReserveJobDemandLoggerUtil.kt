package com.iplease.server.ip.release.infra.log.util

import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ReserveJobDemandLoggerUtil : SimpleLoggerUtil<IpReleaseReserveTable, IpReleaseDemandDto>(
    LoggerFactory.getLogger(ReserveJobDemandLoggerUtil::class.java),
    "[JOB] [해제예약 - 신청]"
) {
    override fun logOnError(throwable: Throwable, uuid: String) {
        log("해제예약중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }

    override fun logOnStart(input: IpReleaseReserveTable, uuid: String) {
        log("해제예약 정보를 토대로 신청등록을 진행합니다.", uuid, true) { info(it) }
        log("해제예약 정보 : $input", uuid) { info(it) }
    }

    override fun logOnComplete(output: IpReleaseDemandDto, uuid: String) {
        log("신청등록을 완료하였습니다.", uuid) { info(it) }
        log("해제신청 정보 : $output", uuid) { info(it) }
    }
}