package com.iplease.server.ip.release.domain.request.data.dto

import com.iplease.server.ip.release.domain.request.data.type.DemandStatusType

data class IpReleaseDemandDto (
    val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val status: DemandStatusType
)
