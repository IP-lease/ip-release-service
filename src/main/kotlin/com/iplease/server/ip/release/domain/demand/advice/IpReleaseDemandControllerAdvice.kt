package com.iplease.server.ip.release.domain.demand.advice

import com.iplease.server.ip.release.domain.demand.controller.IpReleaseDemandController
import com.iplease.server.ip.release.domain.demand.data.response.ErrorResponse
import com.iplease.server.ip.release.domain.demand.data.type.ErrorCode
import com.iplease.server.ip.release.domain.demand.exception.*
import com.iplease.server.ip.release.global.demand.exception.AlreadyDemandedAssignedIpException
import com.iplease.server.ip.release.global.demand.exception.UnknownAssignedIpException
import com.iplease.server.ip.release.global.demand.exception.WrongAccessAssignedIpException
import com.iplease.server.ip.release.global.demand.exception.WrongAccessDemandException
import org.springframework.http.HttpStatus
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
        ErrorResponse(ErrorCode.UN_CANCELABLE_DEMAND, "취소할 수 없는 신청입니다!", "이미 해당 할당IP에 대한 해제신청이 진행중입니다.")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }

    @ExceptionHandler(UnknownAssignedIpException::class)
    fun handle(exception: UnknownAssignedIpException) =
        ErrorResponse(ErrorCode.UNKNOWN_ASSIGNED_IP, "할당IP를 찾을 수 없습니다.", "uuid가 ${exception.uuid}인 할당IP를 찾을 수 없습니다.")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }

    @ExceptionHandler(WrongAccessAssignedIpException::class)
    fun handle(exception: WrongAccessAssignedIpException) =
        ErrorResponse(ErrorCode.WRONG_ACCESS_ASSIGNED_IP, "할당IP에 접근할 수 없습니다!", "할당IP를 소유하고있는지 확인해주세요.")
            .let { ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(it) }
            .let { it.toMono() }

    @ExceptionHandler(WrongAccessDemandException::class)
    fun handle(exception: WrongAccessDemandException) =
        ErrorResponse(ErrorCode.WRONG_ACCESS_DEMAND, "해제신청에 접근할 수 없습니다!", "해제신청을 소유하고있는지 확인해주세요.")
            .let { ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(it) }
            .let { it.toMono() }
}