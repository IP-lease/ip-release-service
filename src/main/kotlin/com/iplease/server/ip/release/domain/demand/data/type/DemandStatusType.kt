package com.iplease.server.ip.release.domain.demand.data.type

enum class DemandStatusType(
    val isCancelable: Boolean,
    val isAcceptable: Boolean
) {
    CREATED(true, true), COMPLETE(false, false);
}
