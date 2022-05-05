package com.iplease.server.ip.release.global.demand.advice

import com.iplease.server.ip.release.domain.demand.data.response.ErrorResponse
import com.iplease.server.ip.release.domain.demand.data.type.ErrorCode
import com.iplease.server.ip.release.global.demand.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.kotlin.core.publisher.toMono

@RestControllerAdvice
class GlobalControllerDemandAdvice {
    @ExceptionHandler(AlreadyDemandedAssignedIpException::class)
    fun handle(exception: AlreadyDemandedAssignedIpException) =
        ErrorResponse(ErrorCode.ALREADY_DEMANDED, "이미 신청하셧습니다!", "이미 해당 할당IP에 대한 해제신청이 진행중입니다.")
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

    @ExceptionHandler(UnknownDemandException::class)
    fun handle(exception: UnknownDemandException) =
        ErrorResponse(ErrorCode.UNKNOWN_DEMAND, "해제신청을 찾을 수 없습니다.", "uuid가 ${exception.uuid}인 할당IP 해제신청을 찾을 수 없습니다.")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }

    @ExceptionHandler(WrongAccessDemandException::class)
    fun handle(exception: WrongAccessDemandException) =
        ErrorResponse(ErrorCode.WRONG_ACCESS_DEMAND, "해제신청에 접근할 수 없습니다!", "해제신청을 소유하고있는지 확인해주세요.")
            .let { ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(it) }
            .let { it.toMono() }
}