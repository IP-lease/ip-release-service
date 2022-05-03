package com.iplease.server.ip.release.domain.demand.service

import com.google.protobuf.Int64Value
import com.google.protobuf.Timestamp
import com.iplease.lib.ip.release.AssignedIp
import com.iplease.lib.ip.release.ReactorIpManageQueryServiceGrpc.ReactorIpManageQueryServiceStub
import com.iplease.server.ip.release.domain.demand.data.dto.AssignedIpDto
import com.iplease.server.ip.release.global.request.service.IpManageQueryService
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

//todo @Service
class GrpcIpManageQueryService(
    private val reactorIpManageQueryServiceStub: ReactorIpManageQueryServiceStub
): IpManageQueryService {
    override fun existsAssignedIpByUuid(uuid: Mono<Long>): Mono<Boolean> =
        uuid.map { Int64Value.newBuilder().setValue(it).build() }
            .let { reactorIpManageQueryServiceStub.existsAssignedIpByUuid(it) }
            .map { it.value }

    override fun getAssignedIpByUuid(uuid: Mono<Long>): Mono<AssignedIpDto> =
        uuid.map { Int64Value.newBuilder().setValue(it).build() }
            .let { reactorIpManageQueryServiceStub.getAssignedIpByUuid(it) }
            .map { it.toAssignedIpDto() }

    private fun AssignedIp.toAssignedIpDto()= AssignedIpDto(uuid, issuerUuid, assignerUuid, assignedAt.toLocalDateTime())
    private fun Timestamp.toLocalDateTime(): LocalDateTime =
        Instant.ofEpochSecond(seconds, nanos.toLong())
            .atZone( ZoneId.of( "Asia/Seoul" ) )
            .toLocalDateTime()
}

