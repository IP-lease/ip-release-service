package com.iplease.server.ip.release.global.common.util

import java.time.LocalDate
import java.time.LocalDateTime

interface DateUtil {
    fun dateNow(): LocalDate
    fun dateTimeNow(): LocalDateTime
}