package com.iplease.server.ip.release.global.log.service

import com.iplease.server.ip.release.global.log.type.LoggingActType
import com.iplease.server.ip.release.global.log.util.ReserveJobDemandLoggerUtil
import com.iplease.server.ip.release.global.log.util.LoggerUtil
import com.iplease.server.ip.release.global.log.util.ReserveJobDeleteLoggerUtil
import org.springframework.stereotype.Service

@Service
class LoggingServiceImpl(
    private val reserveJobDemandLoggerUtil: ReserveJobDemandLoggerUtil,
    private val reserveJobDeleteLoggerUtil: ReserveJobDeleteLoggerUtil
): LoggingService {
    override fun <IN, OUT> getLoggerUtil(type: LoggingActType): LoggerUtil<IN, OUT> =
        when (type) {
            LoggingActType.RESERVE_JOB_DEMAND_LOGGER -> reserveJobDemandLoggerUtil
            LoggingActType.RESERVE_JOB_DELETE_LOGGER -> reserveJobDeleteLoggerUtil
            else -> throw IllegalArgumentException("LoggingActType is not supported.")
        } as LoggerUtil<IN, OUT>
}