package com.iplease.server.ip.release.global.reserve.exception

class WrongAccessReserveException(
    val uuid: Long,
    val issuerUuid: Long
) : RuntimeException("해당 예약에 접근할 권한이 없습니다 - uuid: $uuid")