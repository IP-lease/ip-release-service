package com.iplease.server.ip.release.domain.request.data.type

enum class ErrorCode {
    ALREADY_DEMANDED,
    UN_CANCELABLE_DEMAND,
    PERMISSION_DENIED,
    UNKNOWN_ASSIGNED_IP,
    UNKNOWN_DEMAND,
    WRONG_ACCESS_ASSIGNED_IP,
    WRONG_ACCESS_DEMAND
}
