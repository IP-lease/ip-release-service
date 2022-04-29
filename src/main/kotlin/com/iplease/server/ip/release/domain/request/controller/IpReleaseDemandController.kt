package com.iplease.server.ip.release.domain.request.controller

import com.iplease.server.ip.release.domain.request.data.response.DemandReleaseIpResponse
import com.iplease.server.ip.release.domain.request.exception.*
import com.iplease.server.ip.release.domain.request.service.IpReleaseDemandService
import com.iplease.server.ip.release.global.type.Permission
import com.iplease.server.ip.release.global.type.Role
import com.iplease.server.ip.release.global.grpc.service.IpManageQueryService
import com.iplease.server.ip.release.global.grpc.service.IpReleaseDemandQueryService
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
    private val ipReleaseDemandQueryService: IpReleaseDemandQueryService,
    private val ipManageQueryService: IpManageQueryService
    ) {
    //IP 할당 해제 신청
    @PostMapping("/{assignedIpUuid}")
    fun demandReleaseIp(@PathVariable assignedIpUuid: Long,
                        @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                        @RequestHeader("X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<DemandReleaseIpResponse>> {
        checkPermission(role, Permission.IP_RELEASE_DEMAND)
        checkAssignedIpExists(assignedIpUuid)
        checkAssignedIpAccess(assignedIpUuid, issuerUuid)
        return ipReleaseDemandService.demand(assignedIpUuid, issuerUuid)
            .map { DemandReleaseIpResponse(it.uuid, it.assignedIpUuid, it.issuerUuid, it.status) }
            .map { ResponseEntity.ok(it) }
    }

    //IP 할당 해제 신청 취소
    @DeleteMapping("/{uuid}")
    fun cancelDemandReleaseIp(@PathVariable uuid: Long,
                              @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                              @RequestHeader("X-Login-Account-Role") role: Role): Mono<ResponseEntity<Unit>> {
        checkPermission(role, Permission.IP_RELEASE_DEMAND_CANCEL)
        checkDemandExists(uuid)
        checkDemandAccess(uuid, issuerUuid)
        return ipReleaseDemandService.cancel(uuid, issuerUuid)
            .map { ResponseEntity.ok(it) }
    }

    private fun checkDemandExists(uuid: Long) {
        if(!ipReleaseDemandQueryService.existsDemandByUuid(uuid)) throw UnknownDemandException(uuid)
    }

    private fun checkDemandAccess(uuid: Long, issuerUuid: Long) {
        val demand = ipReleaseDemandQueryService.getDemandByUuid(uuid)
        if(demand.issuerUuid != issuerUuid) throw WrongAccessDemandException(uuid, issuerUuid)
    }

    private fun checkPermission(role: Role, permission: Permission) {
        if(!role.hasPermission(permission)) throw PermissionDeniedException(permission)
    }

    private fun checkAssignedIpExists(assignedIpUuid: Long) {
        if (!ipManageQueryService.existsAssignedIpByUuid(assignedIpUuid)) throw UnknownAssignedIpException(assignedIpUuid)
    }

    private fun checkAssignedIpAccess(assignedIpUuid: Long, issuerUuid: Long) {
        val assignedIp = ipManageQueryService.getAssignedIpByUuid(assignedIpUuid)
        if(assignedIp.issuerUuid != issuerUuid) throw WrongAccessAssignedIpException(assignedIpUuid, issuerUuid)
    }
}
