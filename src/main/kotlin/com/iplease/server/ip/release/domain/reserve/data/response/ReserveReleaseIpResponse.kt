package com.iplease.server.ip.release.domain.reserve.data.response

import java.time.LocalDate

data class ReserveReleaseIpResponse (
    val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val releaseAt: LocalDate
)
