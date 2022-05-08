package com.iplease.server.ip.release.global.log.service

import com.iplease.server.ip.release.global.log.type.LoggingActType
import com.iplease.server.ip.release.global.log.util.LoggerUtil
import reactor.core.publisher.Mono

interface LoggingService {
    fun <IN, OUT> withLog(input: IN, output: Mono<OUT>, type: LoggingActType): Mono<OUT> =
        log(input, output, getLoggerUtil<IN, OUT>(type))
    private fun <IN, OUT> log(input: IN, mono: Mono<OUT>, logger: LoggerUtil<IN, OUT>): Mono<OUT> =
        mono.doOnSubscribe { logger.logOnStart(input) }
            .doOnSuccess { logger.logOnComplete(it) }
            .doOnError { logger.logOnError(it) }

    fun <IN, OUT>getLoggerUtil(type: LoggingActType): LoggerUtil<IN, OUT>
}