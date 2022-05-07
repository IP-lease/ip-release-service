package com.iplease.server.ip.release.domain.admin.advice

import com.iplease.server.ip.release.domain.admin.controller.IpReleaseAdminController
import com.iplease.server.ip.release.domain.admin.exception.NotAcceptableDemandException
import com.iplease.server.ip.release.global.common.data.response.ErrorResponse
import com.iplease.server.ip.release.domain.demand.data.type.ErrorCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.kotlin.core.publisher.toMono

@RestControllerAdvice(basePackageClasses = [IpReleaseAdminController::class])
class IpReleaseAdminControllerAdvice {
    @ExceptionHandler(NotAcceptableDemandException::class)
    fun handle(exception: NotAcceptableDemandException) =
        ErrorResponse(ErrorCode.UN_ACCEPTABLE_DEMAND, "수락할 수 없는 신청입니다!", "이미 수락하신 신청인가요?")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }
}