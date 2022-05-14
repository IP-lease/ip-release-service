package com.iplease.server.ip.release.domain.demand.service

import com.google.protobuf.Int64Value
import com.iplease.lib.ip.manage.AssignedIp
import com.iplease.lib.ip.manage.Date
import com.iplease.lib.ip.manage.ReactorIpManageQueryServiceGrpc.ReactorIpManageQueryServiceStub
import com.iplease.server.ip.release.domain.demand.data.dto.AssignedIpDto
import com.iplease.server.ip.release.global.common.service.IpManageQueryService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
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
    private fun Date.toLocalDateTime(): LocalDate = LocalDate.of(year, month, day)
}

