package com.iplease.server.ip.release.global.request.service

import com.iplease.server.ip.release.domain.request.data.dto.AssignedIpDto

//TODO gRPC 도입예정
interface IpManageQueryService {
    fun existsAssignedIpByUuid(uuid: Long): Boolean
    fun getAssignedIpByUuid(uuid: Long): AssignedIpDto
}
