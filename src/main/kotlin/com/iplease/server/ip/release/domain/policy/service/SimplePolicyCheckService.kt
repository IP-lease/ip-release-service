package com.iplease.server.ip.release.domain.policy.service

import com.iplease.server.ip.release.domain.demand.exception.UnknownAssignedIpException
import com.iplease.server.ip.release.domain.demand.exception.WrongAccessAssignedIpException
import com.iplease.server.ip.release.domain.demand.exception.WrongAccessDemandException
import com.iplease.server.ip.release.global.common.exception.PermissionDeniedException
import com.iplease.server.ip.release.global.common.type.Permission
import com.iplease.server.ip.release.global.common.type.Role
import com.iplease.server.ip.release.global.demand.exception.UnknownDemandException
import com.iplease.server.ip.release.global.demand.service.IpManageQueryService
import com.iplease.server.ip.release.global.demand.service.IpReleaseDemandQueryService
import com.iplease.server.ip.release.global.policy.service.PolicyCheckService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class SimplePolicyCheckService(
    private val ipReleaseDemandQueryService: IpReleaseDemandQueryService,
    private val ipManageQueryService: IpManageQueryService,
): PolicyCheckService {
    override fun checkPermission(role: Role, permission: Permission) =
        if (role.hasPermission(permission)) Mono.just(Any())
        else Mono.defer { Mono.error(PermissionDeniedException(permission)) }

    override fun checkDemandExists(demandUuid: Long) =
        ipReleaseDemandQueryService.existsDemandByUuid(demandUuid.toMono())
            .flatMap {
                if(it) Mono.just(Any())
                else Mono.defer { Mono.error(UnknownDemandException(demandUuid)) }
            }

    override fun checkDemandAccess(demandUuid: Long, accessorUuid: Long) =
        ipReleaseDemandQueryService.getDemandByUuid(demandUuid.toMono())
            .map { it.issuerUuid }
            .flatMap {
                if(it == accessorUuid) Mono.just(Any())
                else Mono.defer { Mono.error(WrongAccessDemandException(demandUuid, accessorUuid)) }
            }

    override fun checkAssignedIpExists(assignedIpUuid: Long) =
        ipManageQueryService.existsAssignedIpByUuid(assignedIpUuid.toMono()).flatMap {
            if (it) Mono.just(Any())
            else Mono.defer { Mono.error(UnknownAssignedIpException(assignedIpUuid)) }
        }

    override fun checkAssignedIpAccess(assignedIpUuid: Long, accessorUuid: Long) =
        ipManageQueryService.getAssignedIpByUuid(assignedIpUuid.toMono())
            .flatMap {
                if(it.issuerUuid == accessorUuid) Mono.just(Any())
                else Mono.defer { Mono.error(WrongAccessAssignedIpException(assignedIpUuid, accessorUuid)) }
            }
}