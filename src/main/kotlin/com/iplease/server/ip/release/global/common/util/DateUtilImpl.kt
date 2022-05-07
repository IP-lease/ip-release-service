package com.iplease.server.ip.release.global.common.util

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class DateUtilImpl: DateUtil {
    override fun dateNow() = LocalDate.now()!!
    override fun dateTimeNow() = LocalDateTime.now()!!
}