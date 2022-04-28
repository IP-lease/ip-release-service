package com.iplease.server.ip.release.domain.request.exception

class NotCancelableDemandException(val uuid: Long) : RuntimeException("취소할 수 없는 신청입니다! - $uuid")