package com.iplease.server.ip.release.infra.event.service

import com.iplease.server.ip.release.infra.event.listener.RabbitEventListener

interface EventSubscribeService {
    fun addListener(listener: RabbitEventListener)
}