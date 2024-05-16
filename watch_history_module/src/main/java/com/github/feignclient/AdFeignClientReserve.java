package com.github.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "adFeignClient", url = "${adFeignClientReserve.url}")
public interface AdFeignClientReserve {
    @PostMapping("/v1/advertisement/{videoId}/{adViewCount}")
    ResponseEntity<String> createAdDetail(
            @PathVariable("videoId") int videoId,
            @PathVariable("adViewCount") int adViewCount
    );

}
