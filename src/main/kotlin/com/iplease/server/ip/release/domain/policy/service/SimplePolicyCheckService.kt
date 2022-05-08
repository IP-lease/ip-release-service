package com.iplease.server.ip.release.domain.policy.service

import com.iplease.server.ip.release.global.demand.exception.UnknownAssignedIpException
import com.iplease.server.ip.release.global.demand.exception.WrongAccessAssignedIpException
import com.iplease.server.ip.release.global.demand.exception.WrongAccessDemandException
import com.iplease.server.ip.release.global.common.exception.PermissionDeniedException
import com.iplease.server.ip.release.global.common.data.type.Permission
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.global.demand.exception.UnknownDemandException
import com.iplease.server.ip.release.global.common.service.IpManageQueryService
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandQueryService
import com.iplease.server.ip.release.infra.policy.service.PolicyCheckService
import com.iplease.server.ip.release.global.reserve.exception.UnknownReserveException
import com.iplease.server.ip.release.global.reserve.exception.WrongAccessReserveException
import com.iplease.server.ip.release.global.reserve.service.IpReleaseReserveQueryService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class SimplePolicyCheckService(
    private val ipReleaseDemandQueryService: IpReleaseDemandQueryService,
    private val ipReleaseReserveQueryService: IpReleaseReserveQueryService,
    private val ipManageQueryService: IpManageQueryService,
): PolicyCheckService {
    override fun checkPermission(role: Role, permission: Permission) =
        if (role.hasPermission(permission)) Mono.just(Any())
        else Mono.defer { Mono.error(PermissionDeniedException(permission)) }

    override fun checkDemandExists(demandUuid: Long) =
        ipReleaseDemandQueryService.existsDemandByUuid(demandUuid.toMono())
            .checkTemplate(UnknownDemandException(demandUuid))

    override fun checkDemandAccess(demandUuid: Long, accessorUuid: Long) =
        ipReleaseDemandQueryService.getDemandByUuid(demandUuid.toMono())
            .map { it.issuerUuid == accessorUuid }
            .checkTemplate(WrongAccessDemandException(demandUuid, accessorUuid))

    override fun checkAssignedIpExists(assignedIpUuid: Long) =
        ipManageQueryService.existsAssignedIpByUuid(assignedIpUuid.toMono())
            .checkTemplate(UnknownAssignedIpException(assignedIpUuid))

    override fun checkAssignedIpAccess(assignedIpUuid: Long, accessorUuid: Long) =
        ipManageQueryService.getAssignedIpByUuid(assignedIpUuid.toMono())
            .map { it.issuerUuid == accessorUuid }
            .checkTemplate(WrongAccessAssignedIpException(assignedIpUuid, accessorUuid))

    override fun checkReserveExists(reserveUuid: Long) =
        ipReleaseReserveQueryService.existsReserveByUuid(reserveUuid.toMono())
            .checkTemplate(UnknownReserveException(reserveUuid))

    override fun checkReserveAccess(reserveUuid: Long, accessorUuid: Long) =
        ipReleaseReserveQueryService.getReserveByUuid(reserveUuid.toMono())
            .map { it.issuerUuid == accessorUuid }
            .checkTemplate(WrongAccessReserveException(reserveUuid, accessorUuid))

    private fun <T: RuntimeException> Mono<Boolean>.checkTemplate(onFailed : T, failedCondition: Boolean = false): Mono<Any> =
        flatMap {
            if (it == failedCondition) Mono.error(onFailed)
            else Mono.just(Any())
        }
}