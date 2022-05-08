package com.iplease.server.ip.release.global.demand.exception

class UnknownDemandException(val uuid: Long) : RuntimeException("할당 해제신청을 찾을 수 없습니다! - uuid: $uuid")
