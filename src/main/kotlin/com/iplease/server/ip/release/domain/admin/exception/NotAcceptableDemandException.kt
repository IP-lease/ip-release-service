package com.iplease.server.ip.release.domain.admin.exception

class NotAcceptableDemandException(val uuid: Long) : RuntimeException("수락할 수 없는 신청입니다! - $uuid")