package com.iplease.server.ip.release.domain.request.controller

import com.iplease.server.ip.release.domain.request.data.response.DemandReleaseIpResponse
import com.iplease.server.ip.release.domain.request.exception.*
import com.iplease.server.ip.release.domain.request.service.IpReleaseDemandService
import com.iplease.server.ip.release.global.type.Permission
import com.iplease.server.ip.release.global.type.Role
import com.iplease.server.ip.release.global.request.service.IpManageQueryService
import com.iplease.server.ip.release.global.request.service.IpReleaseDemandQueryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@RestController
@RequestMapping("/api/v1/ip/release/demand")
class IpReleaseDemandController(
    private val ipReleaseDemandService: IpReleaseDemandService,
    private val ipReleaseDemandQueryService: IpReleaseDemandQueryService,
    private val ipManageQueryService: IpManageQueryService
    ) {
    //IP 할당 해제 신청
    @PostMapping("/{assignedIpUuid}")
    fun demandReleaseIp(@PathVariable assignedIpUuid: Long,
                        @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                        @RequestHeader("X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<DemandReleaseIpResponse>> =
        checkPermission(role, Permission.IP_RELEASE_DEMAND)
            .flatMap { checkAssignedIpExists(assignedIpUuid) }
            .flatMap { checkAssignedIpAccess(assignedIpUuid, issuerUuid) }
            .flatMap { ipReleaseDemandService.demand(assignedIpUuid, issuerUuid) }
            .map { DemandReleaseIpResponse(it.uuid, it.assignedIpUuid, it.issuerUuid, it.status) }
            .map { ResponseEntity.ok(it) }

    //IP 할당 해제 신청 취소
    @DeleteMapping("/{uuid}")
    fun cancelDemandReleaseIp(@PathVariable uuid: Long,
                              @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                              @RequestHeader("X-Login-Account-Role") role: Role): Mono<ResponseEntity<Unit>> =
        checkPermission(role, Permission.IP_RELEASE_DEMAND_CANCEL)
            .flatMap { checkDemandExists(uuid) }
            .flatMap { checkDemandAccess(uuid, issuerUuid) }
            .flatMap { ipReleaseDemandService.cancel(uuid, issuerUuid) }
            .map { ResponseEntity.ok(it) }


    private fun checkDemandExists(uuid: Long) =
        ipReleaseDemandQueryService.existsDemandByUuid(uuid.toMono())
            .flatMap {
                if(it) Mono.just(Unit)
                else Mono.defer { Mono.error(UnknownDemandException(uuid)) }
            }

    private fun checkDemandAccess(uuid: Long, issuerUuid: Long) =
        ipReleaseDemandQueryService.getDemandByUuid(uuid.toMono())
            .map { it.issuerUuid }
            .flatMap {
                if(it == issuerUuid) Mono.just(Unit)
                else Mono.defer { Mono.error(WrongAccessDemandException(uuid, issuerUuid)) }
            }

    private fun checkPermission(role: Role, permission: Permission) =
        if (role.hasPermission(permission)) Mono.just(Unit)
        else Mono.defer { Mono.error(PermissionDeniedException(permission)) }

    private fun checkAssignedIpExists(assignedIpUuid: Long) =
        if (ipManageQueryService.existsAssignedIpByUuid(assignedIpUuid)) Mono.just(Unit)
        else Mono.defer { Mono.error(UnknownAssignedIpException(assignedIpUuid)) }

    private fun checkAssignedIpAccess(assignedIpUuid: Long, issuerUuid: Long) =
        ipManageQueryService.getAssignedIpByUuid(assignedIpUuid)
            .let {
                if(it.issuerUuid == issuerUuid) Mono.just(Unit)
                else Mono.defer { Mono.error(WrongAccessAssignedIpException(assignedIpUuid, issuerUuid)) }
            }
}
