package com.github.controller;

import com.github.common.response.Response;
import com.github.service.AdvertisementService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/advertisement")
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @PostMapping("/{videoId}/{adViewCount}")
    public Response countAdViewCount(
            @PathVariable("videoId") final int videoId,
            @PathVariable("adViewCount") final int adViewCount
    ) {
        advertisementService.countAdViewCount(videoId, adViewCount);
        return Response.success();
    }
}
