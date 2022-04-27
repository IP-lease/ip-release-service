package com.iplease.server.ip.release.domain.request.controller

import com.iplease.server.ip.release.domain.request.service.IpReleaseDemandService
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
        ipReleaseDemandService.demand(assignedIpUuid, issuerUuid)
    }
}
