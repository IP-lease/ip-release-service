package com.iplease.server.ip.release.domain.demand.data.type

enum class DemandStatusType(
    val isCancelable: Boolean
) {
    CREATED(true), COMPLETE(false);
}
