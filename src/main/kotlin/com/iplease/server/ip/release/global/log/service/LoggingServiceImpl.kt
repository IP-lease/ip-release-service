package com.iplease.server.ip.release.global.log.service

import com.iplease.server.ip.release.global.log.type.LoggingActType
import com.iplease.server.ip.release.global.log.util.*
import org.springframework.stereotype.Service

@Service
class LoggingServiceImpl(
    private val reserveJobDemandLoggerUtil: ReserveJobDemandLoggerUtil,
    private val reserveJobDeleteLoggerUtil: ReserveJobDeleteLoggerUtil,
    private val reserveSchedulerLoggerUtil: ReserveSchedulerLoggerUtil,
    private val reserveRequestLoggerUtil: ReserveRequestLoggerUtil,
    private val cancelReserveRequestLoggerUtil: CancelReserveRequestLoggerUtil
): LoggingService {
    override fun <IN, OUT> getLoggerUtil(type: LoggingActType): LoggerUtil<IN, OUT> =
        when (type) {
            LoggingActType.RESERVE_JOB_DEMAND_LOGGER -> reserveJobDemandLoggerUtil
            LoggingActType.RESERVE_JOB_DELETE_LOGGER -> reserveJobDeleteLoggerUtil
            LoggingActType.RESERVE_SCHEDULER_LOGGER -> reserveSchedulerLoggerUtil
            LoggingActType.RESERVE_REQUEST_LOGGER -> reserveRequestLoggerUtil
            LoggingActType.CANCEL_RESERVE_REQUEST_LOGGER -> cancelReserveRequestLoggerUtil
            else -> throw IllegalArgumentException("LoggingActType is not supported.")
        } as LoggerUtil<IN, OUT>
}