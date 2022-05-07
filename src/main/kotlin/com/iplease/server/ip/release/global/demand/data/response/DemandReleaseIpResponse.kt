package com.iplease.server.ip.release.global.demand.data.response

import com.iplease.server.ip.release.domain.demand.data.type.DemandStatusType

data class DemandReleaseIpResponse(
    val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val status: DemandStatusType
)
