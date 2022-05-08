package com.iplease.server.ip.release.infra.log.util

import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.global.demand.data.response.DemandReleaseIpResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class DemandRequestLoggerUtil: SimpleLoggerUtil<DemandRequestInput, ResponseEntity<DemandReleaseIpResponse>>(
    LoggerFactory.getLogger(DemandRequestLoggerUtil::class.java),
    "[RESTAPI] [해제신청 - 등록]"
) {
    override fun logOnStart(input: DemandRequestInput, uuid: String) {
        log("신청등록을 진행합니다.", uuid, true) { info(it) }
        log("요청 정보: $input", uuid) { info(it) }
    }

    override fun logOnComplete(output: ResponseEntity<DemandReleaseIpResponse>, uuid: String) {
        log("신청등록을 완료하였습니다.", uuid) { info(it) }
        log("응답 정보: ${output.body!!}", uuid) { info(it) }
    }

    override fun logOnError(throwable: Throwable, uuid: String) {
        log("신청등록중 오류가 발생하였습니다!", uuid) { warn(it) }
        log("오류 내용: ${throwable.message}", uuid) { warn(it) }
    }
}
data class DemandRequestInput(
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val role: Role
)