package com.iplease.server.ip.release.domain.demand.controller

import com.iplease.server.ip.release.global.demand.data.response.DemandReleaseIpResponse
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandService
import com.iplease.server.ip.release.global.event.service.EventPublishService
import com.iplease.server.ip.release.global.event.type.Event
import com.iplease.server.ip.release.global.common.data.type.Permission
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.global.policy.service.PolicyCheckService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/ip/release/demand")
class IpReleaseDemandController(
    private val ipReleaseDemandService: IpReleaseDemandService,
    private val eventPublishService: EventPublishService,
    private val policyCheckService: PolicyCheckService
) {
    //IP 할당 해제 신청
    @PostMapping("/{assignedIpUuid}") //TODO PathVariable to Payload 전환 고려해보기
    fun demandReleaseIp(@PathVariable assignedIpUuid: Long,
                        @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                        @RequestHeader("X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<DemandReleaseIpResponse>> =
        policyCheckService.run {
            checkPermission(role, Permission.IP_RELEASE_DEMAND)
                .flatMap { checkAssignedIpExists(assignedIpUuid) }
                .flatMap { checkAssignedIpAccess(assignedIpUuid, issuerUuid) }
        }.flatMap { ipReleaseDemandService.demand(assignedIpUuid, issuerUuid) }
            .map { eventPublishService.publish(Event.IP_RELEASE_DEMAND_ADD.routingKey, it) }
            .map { DemandReleaseIpResponse(it.uuid, it.assignedIpUuid, it.issuerUuid, it.status) }
            .map { ResponseEntity.ok(it) }

    //IP 할당 해제 신청 취소
    @DeleteMapping("/{demandUuid}")
    fun cancelDemandReleaseIp(@PathVariable demandUuid: Long,
                              @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                              @RequestHeader("X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<Unit>> =
        policyCheckService.run {
            checkPermission(role, Permission.IP_RELEASE_DEMAND_CANCEL)
                .flatMap { checkDemandExists(demandUuid) }
                .flatMap { checkDemandAccess(demandUuid, issuerUuid) }
        }.flatMap { ipReleaseDemandService.cancel(demandUuid, issuerUuid) }
            .map { ResponseEntity.ok(it) }
}
