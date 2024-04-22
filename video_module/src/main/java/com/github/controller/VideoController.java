package com.github.controller;

import com.github.common.response.Response;
import com.github.controller.response.VideoResponse;
import com.github.controller.response.ViewResponse;
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

    /**
     * 영상 조회수 증가 및 가장 최근의 시청중단 지점 응답
     * @param viewDto
     * @param deviceUUID 로그인을 하지 않은 유저를 식별하는 데에 사용
     * @return
     */
    @PostMapping("/play")
    public Response<ViewResponse> countView(
            @RequestBody final ViewDto viewDto,
            @RequestHeader("X-Device-UUID") final String deviceUUID
    ) {

        viewService.countView(viewDto);
        ViewResponse response = viewService.getLastWatched(viewDto, deviceUUID);
        return Response.success(response);
    }

    /**
     * 재생 중단시점의 정보들로 시청기록 생성
     * Batch 통계를 위해, 행을 업데이트 하는 것이 아니라 계속 신규 생성
     * @param watchHistoryDto
     * @param deviceUUID 로그인을 하지 않은 유저를 식별하는 데에 사용
     * @return
     */
    @PostMapping("/stop")
    public Response<VideoResponse> createWatchHistory(
            @RequestBody final WatchHistoryDto watchHistoryDto,
            @RequestHeader("X-Device-UUID") final String deviceUUID
    ){
        watchHistoryService.createWatchHistory(watchHistoryDto, deviceUUID);
        return Response.success(new VideoResponse("시청기록 생성 성공"));
    }


}
