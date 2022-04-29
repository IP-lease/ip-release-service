package com.iplease.server.ip.release.domain.request.data.response

import com.iplease.server.ip.release.domain.request.data.type.ErrorCode

data class ErrorResponse (
    val status: ErrorCode,
    val title: String,
    val message: String
)