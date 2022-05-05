package com.iplease.server.ip.release.domain.demand.advice

import com.iplease.server.ip.release.domain.demand.controller.IpReleaseDemandController
import com.iplease.server.ip.release.domain.demand.data.response.ErrorResponse
import com.iplease.server.ip.release.domain.demand.data.type.ErrorCode
import com.iplease.server.ip.release.domain.demand.exception.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.kotlin.core.publisher.toMono

@RestControllerAdvice(basePackageClasses = [IpReleaseDemandController::class])
class IpReleaseDemandControllerAdvice {
    @ExceptionHandler(AlreadyDemandedAssignedIpException::class)
    fun handle(exception: AlreadyDemandedAssignedIpException) =
        ErrorResponse(ErrorCode.ALREADY_DEMANDED, "이미 신청하셧습니다!", "이미 해당 할당IP에 대한 해제신청이 진행중입니다.")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }

    @ExceptionHandler(NotCancelableDemandException::class)
    fun handle(exception: NotCancelableDemandException) =
        ErrorResponse(ErrorCode.UN_CANCELABLE_DEMAND, "취소할 수 없는 신청입니다!", "해당신청의 상태를 재확인해주세요.")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }
}