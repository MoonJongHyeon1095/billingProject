package com.github.controller;

import com.github.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/video")
public class VideoController {
    private final VideoService videoService;

    @GetMapping("/play")
    public ResponseBodyEmitter streamVideo() {

        System.out.println("111111");
        return videoService.streamVideoWithExecutor();
    }

}
