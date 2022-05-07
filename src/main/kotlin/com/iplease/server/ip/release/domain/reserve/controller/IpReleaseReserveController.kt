package com.iplease.server.ip.release.domain.reserve.controller

import com.iplease.server.ip.release.domain.reserve.data.response.ReserveReleaseIpResponse
import com.iplease.server.ip.release.domain.reserve.service.IpReleaseReserveService
import com.iplease.server.ip.release.global.common.data.type.Permission
import com.iplease.server.ip.release.global.common.data.type.Role
import com.iplease.server.ip.release.global.policy.service.PolicyCheckService
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDate

@RestController
@RequestMapping("/ip/release/reserve")
class IpReleaseReserveController(
    private val ipReleaseReserveService: IpReleaseReserveService,
    private val policyCheckService: PolicyCheckService
) {
    val LOGGER = LoggerFactory.getLogger(this::class.java)
    val CONTROLLER_PREFIX = "[RESTAPI]"

    @PostMapping("/{assignedIpUuid}")
    fun reserveReleaseIp(@PathVariable assignedIpUuid: Long,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) releaseAt: LocalDate,
                         @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                         @RequestHeader("X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<ReserveReleaseIpResponse>> =
        logReserveRequestStart(assignedIpUuid, releaseAt, issuerUuid, role)
            .flatMap { policyCheckService.checkPermission(role, Permission.IP_RELEASE_RESERVE) }
            .flatMap { policyCheckService.checkAssignedIpExists(assignedIpUuid) }
            .flatMap { policyCheckService.checkAssignedIpAccess(assignedIpUuid, issuerUuid) }
            .flatMap { ipReleaseReserveService.reserve(assignedIpUuid, issuerUuid, releaseAt) }
            .map { ReserveReleaseIpResponse(it.uuid, it.assignedIpUuid, it.issuerUuid, it.releaseAt) }
            .map { ResponseEntity.ok(it) }
            .doOnSuccess { logReserveRequestComplete(it.body!!) }
            .doOnError{ logReserveRequestError(it) }

    private fun logReserveRequestStart(assignedIpUuid: Long, releaseAt: LocalDate, issuerUuid: Long, role: Role): Mono<Unit> {
        val PREFIX = "$CONTROLLER_PREFIX [해제예약 - 등록]"
        LOGGER.info("$PREFIX 예약등록을 진행합니다.")
        LOGGER.info("$PREFIX     요청 정보: $assignedIpUuid, $releaseAt, $issuerUuid, $role")
        return Unit.toMono()
    }

    private fun logReserveRequestComplete(response: ReserveReleaseIpResponse) {
        val PREFIX = "$CONTROLLER_PREFIX [해제예약 - 등록]"
        LOGGER.warn("$PREFIX     예약등록을 완료하였습니다.")
        LOGGER.warn("$PREFIX     응답 정보: $response")
    }

    private fun logReserveRequestError(throwable: Throwable) {
        val PREFIX = "$CONTROLLER_PREFIX [해제예약 - 등록]"
        LOGGER.warn("$PREFIX     예약등록중 오류가 발생하였습니다!")
        LOGGER.warn("$PREFIX     오류 내용: ${throwable.message}")
    }

    @DeleteMapping("/{reserveUuid}")
    fun cancelReserveReleaseIp(@PathVariable reserveUuid: Long,
                               @RequestHeader("X-Login-Account-Uuid") issuerUuid: Long,
                               @RequestHeader("X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<Unit>> =
        logCancelReserveRequestStart(reserveUuid, issuerUuid, role)
            .flatMap { policyCheckService.checkPermission(role, Permission.IP_RELEASE_RESERVE_CANCEL) }
            .flatMap { policyCheckService.checkReserveExists(reserveUuid) }
            .flatMap { policyCheckService.checkReserveAccess(reserveUuid, issuerUuid) }
            .flatMap { ipReleaseReserveService.cancelReserve(reserveUuid) }
            .map { ResponseEntity.ok(it) }
            .doOnSuccess { logCancelReserveRequestComplete() }
            .doOnError{ logCancelReserveRequestError(it) }

    private fun logCancelReserveRequestStart(reserveUuid: Long, issuerUuid: Long, role: Role): Mono<Unit> {
        val PREFIX = "$CONTROLLER_PREFIX [해제예약 - 취소]"
        LOGGER.info("$PREFIX 예약취소를 진행합니다.")
        LOGGER.info("$PREFIX     요청 정보: $reserveUuid, $issuerUuid, $role")
        return Unit.toMono()
    }

    private fun logCancelReserveRequestComplete() {
        val PREFIX = "$CONTROLLER_PREFIX [해제예약 - 취소]"
        LOGGER.warn("$PREFIX     예약취소를 완료하였습니다.")
    }

    private fun logCancelReserveRequestError(throwable: Throwable) {
        val PREFIX = "$CONTROLLER_PREFIX [해제예약 - 취소]"
        LOGGER.warn("$PREFIX     예약취소중 오류가 발생하였습니다!")
        LOGGER.warn("$PREFIX     오류 내용: ${throwable.message}")
    }
}