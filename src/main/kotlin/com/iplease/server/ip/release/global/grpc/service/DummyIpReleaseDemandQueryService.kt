package com.iplease.server.ip.release.global.grpc.service

import com.iplease.server.ip.release.domain.request.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.request.data.type.DemandStatus
import org.springframework.stereotype.Service

@Service //TODO 추후 실제 서비스 구현 후 비활성화 예정
class DummyIpReleaseDemandQueryService: IpReleaseDemandQueryService {
    override fun getDemandByUuid(uuid: Long) = IpReleaseDemandDto(uuid, 0L, 0L, DemandStatus.CREATED)
    override fun existsDemandByUuid(uuid: Long) = true
}