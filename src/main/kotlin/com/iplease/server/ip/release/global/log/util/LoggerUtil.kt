package com.iplease.server.ip.release.global.log.util

interface LoggerUtil<IN, OUT> {
    fun logOnStart(input: IN)
    fun logOnComplete(output: OUT)
    fun logOnError(throwable: Throwable)
}