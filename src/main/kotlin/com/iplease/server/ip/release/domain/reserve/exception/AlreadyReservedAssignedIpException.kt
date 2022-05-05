package com.iplease.server.ip.release.domain.reserve.exception

class AlreadyReservedAssignedIpException(val assignedIpUuid: Long) : RuntimeException("이미 해제 예약된 AssignedIp 입니다! - $assignedIpUuid")