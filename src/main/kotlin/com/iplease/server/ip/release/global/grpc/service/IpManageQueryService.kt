package com.iplease.server.ip.release.global.grpc.service

import com.iplease.server.ip.release.global.grpc.dto.AssignedIp

//TODO gRPC 도입예정
interface IpManageQueryService {
    fun existsAssignedIpByUuid(uuid: Long): Boolean
    fun getAssignedIpByUuid(uuid: Long): AssignedIp
}
