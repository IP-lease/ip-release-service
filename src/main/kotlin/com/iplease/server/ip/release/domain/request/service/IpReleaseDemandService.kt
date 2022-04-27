package com.iplease.server.ip.release.domain.request.service

interface IpReleaseDemandService {
    fun demand(assignedIpUuid: Long, issuerUuid: Long)

}
