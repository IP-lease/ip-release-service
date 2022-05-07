package com.iplease.server.ip.release.global.reserve.advice

import com.iplease.server.ip.release.global.common.data.response.ErrorResponse
import com.iplease.server.ip.release.domain.demand.data.type.ErrorCode
import com.iplease.server.ip.release.global.reserve.exception.UnknownReserveException
import com.iplease.server.ip.release.global.reserve.exception.WrongAccessReserveException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.kotlin.core.publisher.toMono

@RestControllerAdvice
class GlobalControllerReserveAdvice {
    @ExceptionHandler(UnknownReserveException::class)
    fun handle(exception: UnknownReserveException) =
        ErrorResponse(ErrorCode.UNKNOWN_RESERVE, "해제예약을 찾을 수 없습니다.", "uuid가 ${exception.uuid}인 할당IP 해제예약을 찾을 수 없습니다.")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }

    @ExceptionHandler(WrongAccessReserveException::class)
    fun handle(exception: WrongAccessReserveException) =
        ErrorResponse(ErrorCode.WRONG_ACCESS_RESERVE, "해제예약에 접근할 수 없습니다!", "해제예약을 소유하고있는지 확인해주세요.")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }
}