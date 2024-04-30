package com.github.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "adFeignClient", url = "http://localhost:8083")
public interface AdFeignClient {
    @PostMapping("/v1/advertisement/{videoId}/{adViewCount}")
    void createAdDetail(
            @PathVariable("videoId") int videoId,
            @PathVariable("adViewCount") int adViewCount
    );

    @GetMapping("/errorful/case1")
    void case1(
            @PathVariable("videoId") int videoId,
            @PathVariable("adViewCount") int adViewCount
    );
}
