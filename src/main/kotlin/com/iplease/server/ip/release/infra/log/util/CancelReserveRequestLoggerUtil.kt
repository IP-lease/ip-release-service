package com.iplease.server.ip.release.infra.log.util

import com.iplease.server.ip.release.global.common.data.type.Role
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class CancelReserveRequestLoggerUtil: SimpleLoggerUtil<CancelReserveRequestInput, ResponseEntity<Unit>>(
    LoggerFactory.getLogger(CancelReserveRequestLoggerUtil::class.java),
    "[RESTAPI] [해제예약 - 취소]"
) {
    override fun logOnStart(input: CancelReserveRequestInput, uuid: String) {
        log("예약취소를 진행합니다.", uuid, true) { info(it) }
        log("요청 정보: $input", uuid) { info(it) }
    }

    override fun logOnComplete(output: ResponseEntity<Unit>, uuid: String) {
        log("예약취소를 완료하였습니다.", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("예약취소중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}
data class CancelReserveRequestInput(
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val role: Role
)