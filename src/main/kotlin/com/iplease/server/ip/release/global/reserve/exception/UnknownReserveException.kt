package com.iplease.server.ip.release.global.reserve.exception

class UnknownReserveException(val uuid: Long) : RuntimeException("할당 해제예약을 찾을 수 없습니다! - uuid: $uuid")
