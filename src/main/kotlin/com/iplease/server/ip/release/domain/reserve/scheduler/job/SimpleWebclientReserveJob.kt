package com.iplease.server.ip.release.domain.reserve.scheduler.job

import com.iplease.server.ip.release.domain.demand.data.dto.IpReleaseDemandDto
import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import com.iplease.server.ip.release.domain.reserve.repository.IpReleaseReserveRepository
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.global.common.util.DateUtil
import com.iplease.server.ip.release.global.demand.data.response.DemandReleaseIpResponse
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class SimpleWebclientReserveJob(
    reserveRepository: IpReleaseReserveRepository,
    dateUtil: DateUtil,
    private val lbFunction: ReactorLoadBalancerExchangeFilterFunction
) : SimpleReserveJob(reserveRepository, dateUtil) {
    override fun reserve(table: IpReleaseReserveTable): Mono<IpReleaseDemandDto> =
        WebClient.builder()
            .filter(lbFunction)
            .baseUrl("http://ip-release-server")
            .build()
            .post()
            .uri("/api/v1/ip/release/demand/${table.assignedIpUuid}")
            .header("X-Login-Account-Uuid", table.issuerUuid.toString())
            .header("X-Login-Account-Role", Role.ADMINISTRATOR.toString())
            .retrieve()
            .bodyToMono(DemandReleaseIpResponse::class.java)
            .map { IpReleaseDemandDto(it.uuid, it.assignedIpUuid, it.issuerUuid, it.status) }
}