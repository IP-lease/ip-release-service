package com.iplease.server.ip.release.domain.reserve.data.table

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table
data class IpReleaseReserveTable(
    @Id val uuid: Long,
    val assignedIpUuid: Long,
    val issuerUuid: Long,
    val releaseAt: LocalDate
)