package com.iplease.server.ip.release.global.demand.exception

class WrongAccessAssignedIpException(
    val uuid: Long,
    val issuerUuid: Long
) : RuntimeException("해당 할당IP 에 접근할 권한이 없습니다 - uuid: $uuid")
