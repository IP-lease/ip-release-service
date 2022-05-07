package com.iplease.server.ip.release.global.common.exception

import com.iplease.server.ip.release.global.common.data.type.Permission

class PermissionDeniedException(val permission: Permission) : RuntimeException("권한이 없습니다! - $permission") {

}
