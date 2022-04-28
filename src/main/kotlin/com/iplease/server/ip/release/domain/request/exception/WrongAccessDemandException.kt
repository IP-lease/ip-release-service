package com.iplease.server.ip.release.domain.request.exception

class WrongAccessDemandException(
    val uuid: Long,
    val issuerUuid: Long
) : RuntimeException("해당 신청에 접근할 권한이 없습니다 - uuid: $uuid")
