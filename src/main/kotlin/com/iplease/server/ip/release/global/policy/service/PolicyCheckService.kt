package com.iplease.server.ip.release.global.policy.service

import com.iplease.server.ip.release.global.common.type.Permission
import com.iplease.server.ip.release.global.common.type.Role
import reactor.core.publisher.Mono

interface PolicyCheckService {
    fun checkPermission(role: Role, permission: Permission): Mono<Any>
    fun checkDemandExists(demandUuid: Long): Mono<Any>
    fun checkDemandAccess(demandUuid: Long, accessorUuid: Long): Mono<Any>
    fun checkAssignedIpExists(assignedIpUuid: Long): Mono<Any>
    fun checkAssignedIpAccess(assignedIpUuid: Long, accessorUuid: Long): Mono<Any>
    fun checkReserveExists(reserveUuid: Long): Mono<Any>
    fun checkReserveAccess(reserveUuid: Long, accessorUuid: Long): Mono<Any>
}