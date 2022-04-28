package com.iplease.server.ip.release.global.grpc.service

import com.iplease.server.ip.release.domain.request.data.dto.IpReleaseDemandDto

interface IpReleaseDemandQueryService {
    fun getDemandById(uuid: Long): IpReleaseDemandDto
}
