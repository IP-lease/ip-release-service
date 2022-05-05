package com.iplease.server.ip.release.domain.reserve.repository

import com.iplease.server.ip.release.domain.reserve.data.table.IpReleaseReserveTable
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface IpReleaseReserveRepository: R2dbcRepository<IpReleaseReserveTable, Long> {
}