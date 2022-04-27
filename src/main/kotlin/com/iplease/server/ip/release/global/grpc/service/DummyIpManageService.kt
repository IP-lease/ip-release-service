package com.iplease.server.ip.release.global.grpc.service

import com.iplease.server.ip.release.global.grpc.dto.AssignedIp
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service //TODO 나중에 gRPC 적용 후 비활성화
class DummyIpManageService: IpManageService {
    override fun existsAssignedIpByUuid(uuid: Long): Boolean = true
    override fun getAssignedIpByUuid(uuid: Long): AssignedIp = AssignedIp(uuid, 0L, 0L, LocalDateTime.MIN)
}