package com.github.service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private final WebClient webClient;
    private final Map<String, Double> cpuUsageCache = new HashMap<>();

    public MetricsService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://host.docker.internal:9090").build(); // Prometheus 서버 주소
    }

    @Scheduled(fixedRate = 10000) // 10초마다 실행
    public void updateCpuUsageCache() {
        List<String> instanceAddresses = List.of(
                "host.docker.internal:8080",
                "host.docker.internal:8081",
                "host.docker.internal:8082",
                "host.docker.internal:8083",
                "host.docker.internal:8084",
                "host.docker.internal:8085",
                "host.docker.internal:8090",
                "host.docker.internal:8091"
        );

        getCpuUsages(instanceAddresses).subscribe(cpuUsages -> {
            synchronized (cpuUsageCache) {
                cpuUsageCache.clear();
                cpuUsageCache.putAll(cpuUsages);
            }
        });
    }

    public Map<String, Double> getCachedCpuUsages() {
        synchronized (cpuUsageCache) {
            return new HashMap<>(cpuUsageCache);
        }
    }

    private Mono<Map<String, Double>> getCpuUsages(List<String> instanceAddresses) {
        String query = String.format("sum(rate(process_cpu_seconds_total{instance=~\"%s\"}[1m])) by (instance)", String.join("|", instanceAddresses));
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/query")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(PrometheusResponse.class)
                .map(response -> response.data.result.stream()
                        .collect(Collectors.toMap(
                                result -> result.metric.get("instance"),
                                result -> Double.parseDouble(result.value.get(1))
                        )));
    }

    private static class PrometheusResponse {
        public Data data;

        private static class Data {
            public List<Result> result;
        }

        private static class Result {
            public Map<String, String> metric;
            public List<String> value;
        }
    }
}
