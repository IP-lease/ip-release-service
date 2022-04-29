package com.iplease.server.ip.release.domain.request.controller.advice

import com.iplease.server.ip.release.domain.request.data.response.ErrorResponse
import com.iplease.server.ip.release.domain.request.data.type.ErrorCode
import com.iplease.server.ip.release.domain.request.exception.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.kotlin.core.publisher.toMono

@RestControllerAdvice
class IpReleaseDemandControllerAdvice {
    @ExceptionHandler(AlreadyDemandedAssignedIpException::class)
    fun handle(exception: AlreadyDemandedAssignedIpException) =
        ErrorResponse(ErrorCode.ALREADY_DEMANDED, "이미 신청하셧습니다!", "이미 해당 할당IP에 대한 해제신청이 진행중입니다.")
            .let { ResponseEntity.ok(it) }
            .let { it.toMono() }

    @ExceptionHandler(NotCancelableDemandException::class)
    fun handle(exception: NotCancelableDemandException) =
        ErrorResponse(ErrorCode.UN_CANCELABLE_DEMAND, "취소할 수 없는 신청입니다!", "이미 해당 할당IP에 대한 해제신청이 진행중입니다.")
            .let { ResponseEntity.ok(it) }
            .let { it.toMono() }

    @ExceptionHandler(PermissionDeniedException::class)
    fun handle(exception: PermissionDeniedException) =
        ErrorResponse(ErrorCode.PERMISSION_DENIED, "권한이 없습니다!", "해당 작업을 수행하려면 ${exception.permission} 권한이 필요합니다.")
            .let { ResponseEntity.ok(it) }
            .let { it.toMono() }

    @ExceptionHandler(UnknownAssignedIpException::class)
    fun handle(exception: UnknownAssignedIpException) =
        ErrorResponse(ErrorCode.UNKNOWN_ASSIGNED_IP, "할당IP를 찾을 수 없습니다.", "uuid가 ${exception.uuid}인 할당IP를 찾을 수 없습니다.")
            .let { ResponseEntity.ok(it) }
            .let { it.toMono() }

    @ExceptionHandler(UnknownDemandException::class)
    fun handle(exception: UnknownDemandException) =
        ErrorResponse(ErrorCode.UNKNOWN_DEMAND, "해제신청을 찾을 수 없습니다.", "uuid가 ${exception.uuid}인 할당IP 해제신청을 찾을 수 없습니다.")
            .let { ResponseEntity.ok(it) }
            .let { it.toMono() }

    @ExceptionHandler(WrongAccessAssignedIpException::class)
    fun handle(exception: WrongAccessAssignedIpException) =
        ErrorResponse(ErrorCode.WRONG_ACCESS_ASSIGNED_IP, "할당IP에 접근할 수 없습니다!", "할당IP를 소유하고있는지 확인해주세요.")
            .let { ResponseEntity.ok(it) }
            .let { it.toMono() }

    @ExceptionHandler(WrongAccessDemandException::class)
    fun handle(exception: WrongAccessDemandException) =
        ErrorResponse(ErrorCode.WRONG_ACCESS_DEMAND, "해제신청에 접근할 수 없습니다!", "해제신청을 소유하고있는지 확인해주세요.")
            .let { ResponseEntity.ok(it) }
            .let { it.toMono() }
}