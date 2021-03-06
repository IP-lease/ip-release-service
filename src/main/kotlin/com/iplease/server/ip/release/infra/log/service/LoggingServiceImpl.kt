package com.iplease.server.ip.release.infra.log.service

import com.iplease.server.ip.release.infra.log.type.LoggingActType
import com.iplease.server.ip.release.infra.log.util.*
import org.springframework.stereotype.Service

@Service
class LoggingServiceImpl(
    private val reserveJobDemandLoggerUtil: ReserveJobDemandLoggerUtil,
    private val reserveJobDeleteLoggerUtil: ReserveJobDeleteLoggerUtil,
    private val reserveSchedulerLoggerUtil: ReserveSchedulerLoggerUtil,

    private val reserveRequestLoggerUtil: ReserveRequestLoggerUtil,
    private val cancelReserveRequestLoggerUtil: CancelReserveRequestLoggerUtil,
    private val demandRequestLoggerUtil: DemandRequestLoggerUtil,
    private val cancelDemandRequestLoggerUtil: CancelDemandRequestLoggerUtil,

    private val eventPublishLoggerUtil: EventPublishLoggerUtil,
    private val eventSubscribeLoggerUtil: EventSubscribeLoggerUtil,
): LoggingService {
    override fun <IN, OUT> getLoggerUtil(type: LoggingActType): LoggerUtil<IN, OUT> =
        when (type) {
            LoggingActType.RESERVE_JOB_DEMAND_LOGGER -> reserveJobDemandLoggerUtil
            LoggingActType.RESERVE_JOB_DELETE_LOGGER -> reserveJobDeleteLoggerUtil
            LoggingActType.RESERVE_SCHEDULER_LOGGER -> reserveSchedulerLoggerUtil

            LoggingActType.RESERVE_REQUEST_LOGGER -> reserveRequestLoggerUtil
            LoggingActType.CANCEL_RESERVE_REQUEST_LOGGER -> cancelReserveRequestLoggerUtil
            LoggingActType.DEMAND_REQUEST_LOGGER -> demandRequestLoggerUtil
            LoggingActType.CANCEL_DEMAND_REQUEST_LOGGER -> cancelDemandRequestLoggerUtil

            LoggingActType.EVENT_PUBLISH_LOGGER -> eventPublishLoggerUtil
            LoggingActType.EVENT_SUBSCRIBE_LOGGER -> eventSubscribeLoggerUtil
        } as LoggerUtil<IN, OUT>
}