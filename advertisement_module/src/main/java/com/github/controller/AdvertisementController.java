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

    /**
     * 해당 영상에 등록된 광고들 중 재생된 것 조회수 증가
     * @param videoId
     * @param adViewCount
     * @return
     */
    @PostMapping("/{videoId}/{adViewCount}")
    public Response countAdView(
            @PathVariable("videoId") final int videoId,
            @PathVariable("adViewCount") final int adViewCount
    ) {
        advertisementService.countAdView(videoId, adViewCount);
        return Response.success();
    }
}
