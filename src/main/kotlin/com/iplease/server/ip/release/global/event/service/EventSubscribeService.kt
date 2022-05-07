package com.iplease.server.ip.release.global.event.service

import com.iplease.server.ip.release.global.event.listener.RabbitEventListener

interface EventSubscribeService {
    fun addListener(listener: RabbitEventListener)
}