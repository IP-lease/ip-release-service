package com.iplease.server.ip.release.domain.reserve.data.dto

import java.time.LocalDate

data class IpReleaseReserveDto (
    val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val releaseAt: LocalDate
)
