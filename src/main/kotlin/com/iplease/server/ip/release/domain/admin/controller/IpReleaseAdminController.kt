package com.iplease.server.ip.release.domain.admin.controller

import com.iplease.server.ip.release.domain.admin.data.response.AcceptReleaseIpResponse
import com.iplease.server.ip.release.domain.admin.service.IpReleaseAdminService
import com.iplease.server.ip.release.infra.event.service.EventPublishService
import com.iplease.server.ip.release.infra.event.type.Event
import com.iplease.server.ip.release.global.common.data.type.Permission
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.infra.policy.service.PolicyCheckService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/ip/release/admin")
class IpReleaseAdminController(
    private val ipReleaseAdminService: IpReleaseAdminService,
    private val eventPublishService: EventPublishService,
    private val policyCheckService: PolicyCheckService
) {
    //IP 할당 해제 수락
    @PostMapping("/accept/{demandUuid}")
    fun acceptReleaseIp(
        @PathVariable demandUuid: Long,
        @RequestHeader("X-Login-Account-Uuid") operatorUuid: Long,
        @RequestHeader("X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<AcceptReleaseIpResponse>> =
        policyCheckService.run {
            checkPermission(role, Permission.IP_RELEASE_ACCEPT)
                .flatMap { checkDemandExists(demandUuid) }
        }
            .flatMap{ ipReleaseAdminService.acceptDemand(demandUuid, operatorUuid) }
            .map { eventPublishService.publish(Event.IP_RELEASED.routingKey, it) }
            .map { AcceptReleaseIpResponse(it.demandUuid, it.operatorUuid) }
            .map { ResponseEntity.ok(it) }
}
