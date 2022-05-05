package com.iplease.server.ip.release.domain.reserve.controller

import com.iplease.server.ip.release.domain.reserve.response.ReserveReleaseIpResponse
import com.iplease.server.ip.release.domain.reserve.service.IpReleaseReserveService
import com.iplease.server.ip.release.global.common.type.Permission
import com.iplease.server.ip.release.global.common.type.Role
import com.iplease.server.ip.release.global.policy.service.PolicyCheckService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/ip/release/reserve")
class IpReleaseReserveController(
    private val ipReleaseReserveService: IpReleaseReserveService,
    private val policyCheckService: PolicyCheckService
) {
    @PostMapping("/{assignedIpUuid}")
    fun reserveReleaseIp(@PathVariable assignedIpUuid: Long,
                         @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                         @RequestHeader("X-Login-Account-Role") role: Role): Mono<ResponseEntity<ReserveReleaseIpResponse>> =
        policyCheckService.checkPermission(role, Permission.IP_RELEASE_RESERVE)
            .flatMap { policyCheckService.checkAssignedIpExists(assignedIpUuid) }
            .flatMap { policyCheckService.checkAssignedIpAccess(assignedIpUuid, issuerUuid) }
            .flatMap { ipReleaseReserveService.reserve(assignedIpUuid, issuerUuid) }
    .map { ReserveReleaseIpResponse(it.uuid, it.assignedIpUuid, it.issuerUuid, it.releaseAt) }
    .map { ResponseEntity.ok(it) }
}