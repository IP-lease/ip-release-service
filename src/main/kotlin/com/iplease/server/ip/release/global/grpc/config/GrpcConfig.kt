package com.iplease.server.ip.release.global.grpc.config

import com.iplease.lib.ip.release.ReactorIpManageQueryServiceGrpc.ReactorIpManageQueryServiceStub
import com.linecorp.armeria.client.grpc.GrpcClients
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcConfig(
    val discoveryClient: DiscoveryClient
) {
    @Bean
    fun ipManageQueryService(): ReactorIpManageQueryServiceStub {
        //TODO 나중에 scale out 으로 ip manage server 에 대한 CloudLoadbalancing 이 필요할 경우 추천하지 않는 방법
        discoveryClient.getInstances("ip-manage-server").let {
            return GrpcClients.newClient(it[0].uri, ReactorIpManageQueryServiceStub::class.java)
        }
    }
}