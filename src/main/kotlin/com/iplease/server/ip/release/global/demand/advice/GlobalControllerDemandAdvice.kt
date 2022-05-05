package com.iplease.server.ip.release.global.demand.advice

import com.iplease.server.ip.release.domain.demand.data.response.ErrorResponse
import com.iplease.server.ip.release.domain.demand.data.type.ErrorCode
import com.iplease.server.ip.release.global.demand.exception.UnknownDemandException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.kotlin.core.publisher.toMono

@RestControllerAdvice
class GlobalControllerDemandAdvice {
    @ExceptionHandler(UnknownDemandException::class)
    fun handle(exception: UnknownDemandException) =
        ErrorResponse(ErrorCode.UNKNOWN_DEMAND, "해제신청을 찾을 수 없습니다.", "uuid가 ${exception.uuid}인 할당IP 해제신청을 찾을 수 없습니다.")
            .let { ResponseEntity.badRequest().body(it) }
            .let { it.toMono() }
}