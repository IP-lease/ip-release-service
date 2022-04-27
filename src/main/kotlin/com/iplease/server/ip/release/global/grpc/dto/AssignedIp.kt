package com.iplease.server.ip.release.global.grpc.dto

import java.time.LocalDateTime

data class AssignedIp(
    val uuid: Long,
    val issuerUuid: Long,
    val assignerUuid: Long,
    val assignedAt: LocalDateTime
)
