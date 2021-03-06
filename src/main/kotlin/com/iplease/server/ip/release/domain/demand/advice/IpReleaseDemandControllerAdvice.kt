package com.iplease.server.ip.release.domain.demand.advice

import com.iplease.server.ip.release.domain.demand.controller.IpReleaseDemandController
import com.iplease.server.ip.release.global.common.data.response.ErrorResponse
import com.iplease.server.ip.release.domain.demand.data.type.ErrorCode
import com.iplease.server.ip.release.domain.demand.exception.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.kotlin.core.publisher.toMono

@RestControllerAdvice(basePackageClasses = [IpReleaseDemandController::class])
class IpReleaseDemandControllerAdvice {
    @ExceptionHandler(NotCancelableDemandException::class)
    fun handle(exception: NotCancelableDemandException) =
        ErrorResponse(ErrorCode.UN_CANCELABLE_DEMAND, "취소할 수 없는 신청입니다!", "해당신청의 상태를 재확인해주세요.")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }
}