package com.iplease.server.ip.release.domain.reserve.exception

class ReleaseAtTodayException(reserveUuid: Long) : RuntimeException("오늘 해제되는 예약은 취소할 수 없습니다! - $reserveUuid")