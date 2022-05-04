package com.iplease.server.ip.release.global.event.type

enum class Event(
    val routingKey: String
) {
    IP_RELEASE_DEMAND_ADD("v1.event.ip.release.offer.add"),
    IP_RELEASED("v1.event.ip.release.released")
}
