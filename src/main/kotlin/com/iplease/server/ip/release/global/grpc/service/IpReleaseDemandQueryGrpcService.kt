package com.iplease.server.ip.release.global.grpc.service

import com.google.protobuf.BoolValue
import com.google.protobuf.Int64Value
import com.iplease.lib.ip.release.DemandStatus
import com.iplease.lib.ip.release.IpReleaseDemand
import com.iplease.lib.ip.release.ReactorIpReleaseDemandQueryServiceGrpc.IpReleaseDemandQueryServiceImplBase
import com.iplease.server.ip.release.domain.request.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.request.data.type.DemandStatusType
import com.iplease.server.ip.release.global.request.service.IpReleaseDemandQueryService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class IpReleaseDemandQueryGrpcService(
    val ipReleaseDemandQueryService: IpReleaseDemandQueryService
): IpReleaseDemandQueryServiceImplBase() {
    override fun getDemandByUuid(request: Mono<Int64Value>): Mono<IpReleaseDemand> =
        ipReleaseDemandQueryService.getDemandByUuid(request.map { it.value })
            .map { it.toGrpcIpReleaseDemand() }

    override fun existsDemandByUuid(request: Mono<Int64Value>): Mono<BoolValue> =
        ipReleaseDemandQueryService.existsDemandByUuid(request.map { it.value })
            .map { BoolValue.newBuilder().setValue(it).build() }

    //TODO 나중에 Mapper 로 뺄지 고민해보기
    private fun IpReleaseDemandDto.toGrpcIpReleaseDemand() =
        IpReleaseDemand.newBuilder()
            .setUuid(uuid)
            .setIssuerUuid(issuerUuid)
            .setAssignedIpUuid(assignedIpUuid)
            .setStatus(status.toGrpcDemandStatus())
            .build()
    private fun DemandStatusType.toGrpcDemandStatus(): DemandStatus {
        DemandStatus.values()
            .forEach { if (it.name == name) return it }
        throw IllegalArgumentException("Unknown demand status: $this")
    }
}


