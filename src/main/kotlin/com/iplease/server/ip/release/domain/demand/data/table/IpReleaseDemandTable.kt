package com.iplease.server.ip.release.domain.demand.data.table

import com.iplease.server.ip.release.domain.demand.data.type.DemandStatusType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class IpReleaseDemandTable(
    @Id val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val status: DemandStatusType
)