package com.iplease.server.ip.release.domain.reserve.advice

import com.iplease.server.ip.release.global.common.data.response.ErrorResponse
import com.iplease.server.ip.release.domain.demand.data.type.ErrorCode
import com.iplease.server.ip.release.domain.reserve.controller.IpReleaseReserveController
import com.iplease.server.ip.release.domain.reserve.exception.AlreadyReservedAssignedIpException
import com.iplease.server.ip.release.domain.reserve.exception.OutOfRangeReleaseDateException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.kotlin.core.publisher.toMono

@RestControllerAdvice(basePackageClasses = [IpReleaseReserveController::class])
class IpReleaseReserveControllerAdvice {
    @ExceptionHandler(AlreadyReservedAssignedIpException::class)
    fun handle(exception: AlreadyReservedAssignedIpException) =
        ErrorResponse(ErrorCode.ALREADY_RESERVED, "이미 예약하셧습니다!", "이미 해당 할당IP에 대한 해제예약이 등록되었습니다.")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }

    @ExceptionHandler(OutOfRangeReleaseDateException::class)
    fun handle(exception: OutOfRangeReleaseDateException) =
        ErrorResponse(ErrorCode.OUT_OF_RANGE_RESERVE_RELEASE_DATE, "해제 예약이 가능한 날짜가 아닙니다!", "해제예약일은 명일 이후 금년말일까지 가능합니다.")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }
}