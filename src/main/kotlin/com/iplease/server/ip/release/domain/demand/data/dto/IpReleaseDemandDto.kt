package com.iplease.server.ip.release.domain.demand.data.dto

import com.iplease.server.ip.release.domain.demand.data.type.DemandStatusType

data class IpReleaseDemandDto (
    val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val status: DemandStatusType
)
