package com.iplease.server.ip.release.infra.log.util

import com.iplease.server.ip.release.domain.reserve.data.response.ReserveReleaseIpResponse
import com.iplease.server.ip.release.global.common.data.type.Role
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ReserveRequestLoggerUtil: SimpleLoggerUtil<ReserveRequestInput, ResponseEntity<ReserveReleaseIpResponse>>(
    LoggerFactory.getLogger(ReserveRequestLoggerUtil::class.java),
    "[RESTAPI] [해제예약 - 등록]"
) {
    override fun logOnStart(input: ReserveRequestInput, uuid: String) {
        log("예약등록을 진행합니다.", uuid, true) { info(it) }
        log("요청 정보: $input", uuid) { info(it) }
    }

    override fun logOnComplete(output: ResponseEntity<ReserveReleaseIpResponse>, uuid: String) {
        log("예약등록을 완료하였습니다.", uuid) { info(it) }
        log("응답 정보: ${output.body!!}", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("예약등록중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}
data class ReserveRequestInput(
    val assignedIpUuid: Long,
    val releaseAt: LocalDate,
    val issuerUuid: Long,
    val role: Role
)