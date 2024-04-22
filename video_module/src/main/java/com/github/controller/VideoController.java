package com.github.controller;

import com.github.common.response.Response;
import com.github.controller.response.VideoResponse;
import com.github.dto.WatchHistoryDto;
import com.github.dto.ViewDto;
import com.github.service.ViewService;
import com.github.service.WatchHistoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/v1/video")
public class VideoController {
    private final ViewService viewService;
    private final WatchHistoryService watchHistoryService;

    @PostMapping("/play/{videoId}")
    public Response<VideoResponse> countView(
            @PathVariable("videoId") final int videoId
    ) {
        ViewDto viewDto = new ViewDto(videoId);
        viewService.countView(viewDto);
        return Response.success(new VideoResponse("조회수 업데이트 성공"));
    }

    @PostMapping("/stop")
    public Response<VideoResponse> createWatchHistory(
            @RequestBody final WatchHistoryDto watchHistoryDto,
            @RequestHeader("X-Device-UUID") final String deviceUUID
    ){
        watchHistoryService.createWatchHistory(watchHistoryDto, deviceUUID);
        return Response.success(new VideoResponse("시청기록 생성 성공"));
    }

}
