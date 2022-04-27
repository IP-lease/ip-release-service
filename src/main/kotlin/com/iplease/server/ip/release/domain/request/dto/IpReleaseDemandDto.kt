package com.iplease.server.ip.release.domain.request.dto

import com.iplease.server.ip.release.domain.request.type.DemandStatus

data class IpReleaseDemandDto (
    val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val status: DemandStatus
)
