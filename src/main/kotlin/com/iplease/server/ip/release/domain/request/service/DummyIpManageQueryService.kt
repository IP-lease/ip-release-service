package com.iplease.server.ip.release.domain.request.service

import com.iplease.server.ip.release.domain.request.data.dto.AssignedIpDto
import com.iplease.server.ip.release.global.request.service.IpManageQueryService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service //TODO 나중에 gRPC 적용 후 비활성화
class DummyIpManageQueryService: IpManageQueryService {
    override fun existsAssignedIpByUuid(uuid: Long): Boolean = true
    override fun getAssignedIpByUuid(uuid: Long): AssignedIpDto = AssignedIpDto(uuid, 0L, 0L, LocalDateTime.MIN)
}