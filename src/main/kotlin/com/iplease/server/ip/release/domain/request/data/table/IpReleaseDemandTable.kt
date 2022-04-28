package com.iplease.server.ip.release.domain.request.data.table

import com.iplease.server.ip.release.domain.request.data.type.DemandStatus
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class IpReleaseDemandTable(
    @Id val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val status: DemandStatus
)