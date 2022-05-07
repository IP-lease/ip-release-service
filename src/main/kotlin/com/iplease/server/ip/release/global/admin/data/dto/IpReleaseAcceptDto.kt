package com.iplease.server.ip.release.global.admin.data.dto

data class IpReleaseAcceptDto (
    val assignedIpUuid: Long,
    val demandUuid: Long,
    val operatorUuid: Long
)
