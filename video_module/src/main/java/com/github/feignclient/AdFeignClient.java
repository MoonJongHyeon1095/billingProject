package com.github.feignclient;

import com.github.domain.WatchHistory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "adFeignClient", url = "http://localhost:8082")
public interface AdFeignClient {
    @RequestMapping(method = RequestMethod.POST, value="/v1/advertisement/")
    void createAdDetail(@RequestBody WatchHistory watchHistory);
}
