package com.github.controller;

import com.github.common.response.Response;
import com.github.controller.response.ViewResponse;
import com.github.dto.StopDto;
import com.github.dto.ViewDto;
import com.github.service.StreamService;
import com.github.service.ViewService;
import com.github.service.WatchHistoryService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/v1/video")
public class VideoController {
    private final ViewService viewService;
    private final WatchHistoryService watchHistoryService;

    @PostMapping("/play/{videoId}")
    public Response<ViewResponse> countView(@PathVariable("videoId") ViewDto viewDto) {
        viewService.countView(viewDto);
        return Response.success(new ViewResponse());
    }

    @PostMapping("/stop/{videoId}/{stoppedTime}")
    public Response createWatchHistory(@PathVariable("stoppedTime") StopDto stopDto){
        watchHistoryService.createWatchHistory(stopDto);
        return Response.success();
    }

}
