package com.iplease.server.ip.release.domain.request.controller

import com.iplease.server.ip.release.domain.request.exception.PermissionDeniedException
import com.iplease.server.ip.release.domain.request.exception.UnknownAssignedIpException
import com.iplease.server.ip.release.domain.request.exception.WrongAccessAssignedIpException
import com.iplease.server.ip.release.domain.request.service.IpReleaseDemandService
import com.iplease.server.ip.release.global.Permission
import com.iplease.server.ip.release.global.Role
import com.iplease.server.ip.release.global.grpc.service.IpManageService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

class IpReleaseDemandController(
    val ipReleaseDemandService: IpReleaseDemandService,
    val ipManageService: IpManageService
    ) {
    //IP 할당 해제 신청
    fun demandReleaseIp(@PathVariable assignedIpUuid: Long,
                        @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                        @RequestHeader("X-Login-Account-Role") role: Role) {
        checkPermission(role, Permission.IP_RELEASE_DEMAND)
        checkAssignedIpExists(assignedIpUuid)
        checkAssignedIpAccess(assignedIpUuid, issuerUuid)
        ipReleaseDemandService.demand(assignedIpUuid, issuerUuid)
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
