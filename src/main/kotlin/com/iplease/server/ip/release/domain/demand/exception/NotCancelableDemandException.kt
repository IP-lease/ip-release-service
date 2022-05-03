package com.iplease.server.ip.release.domain.demand.exception

class NotCancelableDemandException(val uuid: Long) : RuntimeException("취소할 수 없는 신청입니다! - $uuid")