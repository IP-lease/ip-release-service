package com.iplease.server.ip.release.global.demand.exception

class UnknownAssignedIpException(val uuid: Long) : RuntimeException("할당IP 를 찾을 수 없습니다! - uuid: $uuid")
