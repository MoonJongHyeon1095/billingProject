package com.github.controller;

import com.github.dto.StreamDto;
import com.github.service.StreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/video")
public class VideoController {
    private final StreamService streamService;

    @GetMapping("/stream/{videoId}")
    public ResponseBodyEmitter streamVideo(@PathVariable("videoId") int videoId) {
        return streamService.streamVideoWithExecutor(new StreamDto(videoId));
    }

}
