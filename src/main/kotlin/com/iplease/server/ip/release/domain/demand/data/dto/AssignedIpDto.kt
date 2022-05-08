package com.iplease.server.ip.release.domain.demand.data.dto

import java.time.LocalDateTime

data class AssignedIpDto(
    val uuid: Long,
    val issuerUuid: Long,
    val assignerUuid: Long,
    val assignedAt: LocalDateTime
)
