package com.iplease.server.ip.release.domain.request.repository

import com.iplease.server.ip.release.domain.request.table.IpReleaseDemandTable
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface IpReleaseDemandRepository: R2dbcRepository<IpReleaseDemandTable, Long> {
}