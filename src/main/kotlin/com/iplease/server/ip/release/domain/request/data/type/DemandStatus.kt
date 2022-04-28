package com.iplease.server.ip.release.domain.request.data.type

enum class DemandStatus(
    val isCancelable: Boolean
) {
    CREATED(true), COMPLETE(false);
}
