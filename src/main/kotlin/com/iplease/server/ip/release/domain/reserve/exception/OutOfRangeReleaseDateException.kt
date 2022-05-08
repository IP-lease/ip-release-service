package com.iplease.server.ip.release.domain.reserve.exception

import java.time.LocalDate

class OutOfRangeReleaseDateException(val releaseDate: LocalDate) : RuntimeException("해제 예약일 범위를 벗어났습니다! - $releaseDate")
