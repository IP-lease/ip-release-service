package com.iplease.server.ip.release.global.log.service

import com.iplease.server.ip.release.global.log.type.LoggingActType
import com.iplease.server.ip.release.global.log.util.DemandJobLoggerUtil
import com.iplease.server.ip.release.global.log.util.LoggerUtil
import org.springframework.stereotype.Service

@Service
class LoggingServiceImpl(
    private val demandJobLoggerUtil: DemandJobLoggerUtil,
): LoggingService {
    override fun <IN, OUT> getLoggerUtil(type: LoggingActType): LoggerUtil<IN, OUT> =
        when (type) {
            LoggingActType.DEMAND_JOB_LOGGER -> demandJobLoggerUtil
            else -> throw IllegalArgumentException("LoggingActType is not supported.")
        } as LoggerUtil<IN, OUT>
}