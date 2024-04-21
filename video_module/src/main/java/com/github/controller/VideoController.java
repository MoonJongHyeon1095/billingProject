package com.github.controller;

import com.github.common.response.Response;
import com.github.controller.response.ViewResponse;
import com.github.dto.StopDto;
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
    public Response<ViewResponse> countView(
            @PathVariable("videoId") final ViewDto viewDto
    ) {
        viewService.countView(viewDto);
        return Response.success(new ViewResponse());
    }

    @PutMapping("/stop")
    public Response createWatchHistory(
            @RequestBody final StopDto stopDto,
            @RequestHeader("X-Device-UUID") final String deviceUUID
    ){
        watchHistoryService.createWatchHistory(stopDto, deviceUUID);
        return Response.success();
    }

}
