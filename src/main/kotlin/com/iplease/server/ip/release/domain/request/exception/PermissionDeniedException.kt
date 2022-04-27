package com.iplease.server.ip.release.domain.request.exception

import com.iplease.server.ip.release.global.Permission

class PermissionDeniedException(val permission: Permission) : RuntimeException("권한이 없습니다! - $permission") {

}
