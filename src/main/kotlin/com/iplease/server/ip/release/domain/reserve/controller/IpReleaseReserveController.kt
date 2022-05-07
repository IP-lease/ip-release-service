package com.iplease.server.ip.release.domain.reserve.controller

import com.iplease.server.ip.release.domain.reserve.data.response.ReserveReleaseIpResponse
import com.iplease.server.ip.release.domain.reserve.service.IpReleaseReserveService
import com.iplease.server.ip.release.global.common.data.type.Permission
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.global.policy.service.PolicyCheckService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.time.LocalDate

@RestController
@RequestMapping("/ip/release/reserve")
class IpReleaseReserveController(
    private val ipReleaseReserveService: IpReleaseReserveService,
    private val policyCheckService: PolicyCheckService
) {
    @PostMapping("/{assignedIpUuid}")
    fun reserveReleaseIp(@PathVariable assignedIpUuid: Long,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) releaseAt: LocalDate,
                         @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                         @RequestHeader("X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<ReserveReleaseIpResponse>> =
        policyCheckService.checkPermission(role, Permission.IP_RELEASE_RESERVE)
            .flatMap { policyCheckService.checkAssignedIpExists(assignedIpUuid) }
            .flatMap { policyCheckService.checkAssignedIpAccess(assignedIpUuid, issuerUuid) }
            .flatMap { ipReleaseReserveService.reserve(assignedIpUuid, issuerUuid, releaseAt) }
            .map { ReserveReleaseIpResponse(it.uuid, it.assignedIpUuid, it.issuerUuid, it.releaseAt) }
            .map { ResponseEntity.ok(it) }

    @DeleteMapping("/{reserveUuid}")
    fun cancelReserveReleaseIp(@PathVariable reserveUuid: Long,
                               @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                               @RequestHeader("X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<Unit>> =
        policyCheckService.checkPermission(role, Permission.IP_RELEASE_RESERVE_CANCEL)
            .flatMap { policyCheckService.checkReserveExists(reserveUuid) }
            .flatMap { policyCheckService.checkReserveAccess(reserveUuid, issuerUuid) }
            .flatMap { ipReleaseReserveService.cancelReserve(reserveUuid) }
            .map { ResponseEntity.ok(it) }
}