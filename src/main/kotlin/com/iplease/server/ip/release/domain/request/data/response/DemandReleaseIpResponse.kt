package com.iplease.server.ip.release.domain.request.data.response

import com.iplease.server.ip.release.domain.request.data.type.DemandStatus

data class DemandReleaseIpResponse(
    val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val status: DemandStatus
)
