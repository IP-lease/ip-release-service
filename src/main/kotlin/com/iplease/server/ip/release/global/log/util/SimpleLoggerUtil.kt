package com.iplease.server.ip.release.global.log.util

import org.slf4j.Logger

abstract class SimpleLoggerUtil<IN, OUT>(
    private val logger: Logger,
    private val prefix: String
): LoggerUtil<IN, OUT> {
    private fun format(msg: String) = "$prefix $msg"
    protected fun log(msg: String, isStart: Boolean = false, func: Logger.(String) -> Unit) =
        (if(isStart) msg else "    $msg").let { logger.func(format(it)) }
}