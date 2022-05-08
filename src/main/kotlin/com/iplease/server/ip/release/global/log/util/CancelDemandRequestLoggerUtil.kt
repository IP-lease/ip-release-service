package com.iplease.server.ip.release.global.log.util

import com.iplease.server.ip.release.global.common.data.type.Role
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class CancelDemandRequestLoggerUtil: SimpleLoggerUtil<CancelDemandRequestInput, ResponseEntity<Unit>>(
    LoggerFactory.getLogger(CancelDemandRequestLoggerUtil::class.java),
    "[RESTAPI] [해제신청 - 취소]"
) {
    override fun logOnStart(input: CancelDemandRequestInput, uuid: String) {
        log("신청취소를 진행합니다.", uuid, true) { info(it) }
        log("요청 정보: $input", uuid) { info(it) }
    }

    override fun logOnComplete(output: ResponseEntity<Unit>, uuid: String) {
        log("신청취소를 완료하였습니다.", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("신청취소중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}
data class CancelDemandRequestInput(
    val demandId: Long,
    val issuerId: Long,
    val role: Role
)
