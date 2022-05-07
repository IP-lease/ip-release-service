package com.iplease.server.ip.release.global.common.data.response

import com.iplease.server.ip.release.domain.demand.data.type.ErrorCode

data class ErrorResponse (
    val status: ErrorCode,
    val title: String,
    val message: String
)