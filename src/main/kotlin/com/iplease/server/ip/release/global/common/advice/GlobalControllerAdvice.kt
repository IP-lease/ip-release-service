package com.iplease.server.ip.release.global.common.advice

import com.iplease.server.ip.release.domain.demand.data.response.ErrorResponse
import com.iplease.server.ip.release.domain.demand.data.type.ErrorCode
import com.iplease.server.ip.release.global.common.exception.PermissionDeniedException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.kotlin.core.publisher.toMono

@RestControllerAdvice
class GlobalControllerAdvice {
    @ExceptionHandler(PermissionDeniedException::class)
    fun handle(exception: PermissionDeniedException) =
        ErrorResponse(ErrorCode.PERMISSION_DENIED, "권한이 없습니다!", "해당 작업을 수행하려면 ${exception.permission} 권한이 필요합니다.")
            .let { ResponseEntity.ok(it) }
            .let { it.toMono() }
}