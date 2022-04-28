package com.iplease.server.ip.release.domain.request.controller

import com.iplease.server.ip.release.domain.request.exception.PermissionDeniedException
import com.iplease.server.ip.release.domain.request.exception.UnknownAssignedIpException
import com.iplease.server.ip.release.domain.request.exception.WrongAccessAssignedIpException
import com.iplease.server.ip.release.domain.request.response.DemandReleaseIpResponse
import com.iplease.server.ip.release.domain.request.service.IpReleaseDemandService
import com.iplease.server.ip.release.global.type.Permission
import com.iplease.server.ip.release.global.type.Role
import com.iplease.server.ip.release.global.grpc.service.IpManageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import reactor.core.publisher.Mono

class IpReleaseDemandController(
    private val ipReleaseDemandService: IpReleaseDemandService,
    private val ipManageService: IpManageService
    ) {
    //IP 할당 해제 신청
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

    private fun checkPermission(role: Role, permission: Permission) {
        if(!role.hasPermission(permission)) throw PermissionDeniedException(permission)
    }

    private fun checkAssignedIpExists(assignedIpUuid: Long) {
        if (!ipManageService.existsAssignedIpByUuid(assignedIpUuid)) throw UnknownAssignedIpException(assignedIpUuid)
    }

    private fun checkAssignedIpAccess(assignedIpUuid: Long, issuerUuid: Long) {
        val assignedIp = ipManageService.getAssignedIpByUuid(assignedIpUuid)
        if(assignedIp.issuerUuid != issuerUuid) throw WrongAccessAssignedIpException(assignedIpUuid, issuerUuid)
    }
}
