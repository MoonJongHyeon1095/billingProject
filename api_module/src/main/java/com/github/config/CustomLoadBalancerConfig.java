package com.github.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;

@Configuration
@LoadBalancerClients({
        @LoadBalancerClient(name = "WATCH-HISTORY", configuration = CustomLoadBalancerConfig.class),
        @LoadBalancerClient(name = "VIDEO-INFO", configuration = CustomLoadBalancerConfig.class),
        @LoadBalancerClient(name = "VIEW", configuration = CustomLoadBalancerConfig.class),
        @LoadBalancerClient(name = "USER", configuration = CustomLoadBalancerConfig.class)
})
public class CustomLoadBalancerConfig {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> roundRobinLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory
    ) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RoundRobinLoadBalancer(loadBalancerClientFactory
                .getLazyProvider(name, ServiceInstanceListSupplier.class), name);
    }
}