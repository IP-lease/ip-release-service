package com.iplease.server.ip.release.infra.log.util

interface LoggerUtil<IN, OUT> {
    fun logOnStart(input: IN, uuid: String)
    fun logOnComplete(output: OUT, uuid: String)
    fun logOnError(throwable: Throwable, uuid: String)
}