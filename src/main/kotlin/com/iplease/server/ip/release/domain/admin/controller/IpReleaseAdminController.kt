package com.iplease.server.ip.release.domain.admin.controller

import com.iplease.server.ip.release.domain.admin.response.AcceptReleaseIpResponse
import com.iplease.server.ip.release.domain.admin.service.IpReleaseAdminService
import com.iplease.server.ip.release.global.demand.exception.UnknownDemandException
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandQueryService
import com.iplease.server.ip.release.global.event.service.EventPublishService
import com.iplease.server.ip.release.global.event.type.Event
import com.iplease.server.ip.release.global.exception.PermissionDeniedException
import com.iplease.server.ip.release.global.type.Permission
import com.iplease.server.ip.release.global.type.Role
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@RestController
@RequestMapping("/api/v1/ip/release/admin")
class IpReleaseAdminController(
    private val ipReleaseAdminService: IpReleaseAdminService,
    private val ipReleaseDemandQueryService: IpReleaseDemandQueryService,
    private val eventPublishService: EventPublishService
) {
    //IP 할당 해제 수락
    @PostMapping("/accept/{demandUuid}")
    fun acceptReleaseIp(
        @PathVariable demandUuid: Long,
        @RequestHeader("X-Login-Account-Uuid") operatorUuid: Long,
        @RequestHeader("X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<AcceptReleaseIpResponse>> =
        checkPermission(role, Permission.IP_RELEASE_ACCEPT)
            .flatMap { checkDemandExists(demandUuid) }
            .flatMap{ ipReleaseAdminService.acceptDemand(demandUuid, operatorUuid) }
            .map { eventPublishService.publish(Event.IP_RELEASED.routingKey, it) }
            .map { AcceptReleaseIpResponse(it.demandUuid, it.operatorUuid) }
            .map { ResponseEntity.ok(it) }

    private fun checkDemandExists(demandUuid: Long) =
        ipReleaseDemandQueryService.existsDemandByUuid(demandUuid.toMono())
            .flatMap {
                if(it) Mono.just(Unit)
                else Mono.error(UnknownDemandException(demandUuid))
            }

    private fun checkPermission(role: Role, permission: Permission) =
        if(role.hasPermission(permission)) Mono.just(Unit)
        else Mono.error(PermissionDeniedException(permission))
}
