package com.github.controller;

import com.github.common.response.Response;
import com.github.controller.response.WatchHistoryResponse;
import com.github.dto.WatchHistoryDto;
import com.github.service.WatchHistoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/v1/video")
public class WatchHistoryController {
    private final WatchHistoryService watchHistoryService;

    /**
     * 재생 중단시점의 정보들로 시청기록 생성
     * Batch 통계를 위해, 행을 업데이트 하는 것이 아니라 계속 신규 생성
     * @param watchHistoryDto
     * @param deviceUUID 로그인을 하지 않은 유저를 식별하는 데에 사용
     * @return
     */
    @PostMapping("/stop")
    public Response<WatchHistoryResponse> createWatchHistory(
            @RequestBody final WatchHistoryDto watchHistoryDto,
            @RequestHeader("X-User-Email") final String email,
            @RequestHeader("X-Device-UUID") final String deviceUUID
    ){
        log.info(email+deviceUUID);
        if (email!=null) {
            watchHistoryDto.setEmail(Optional.of(email));
        } else {
            // 로그인하지 않은 사용자일 경우, email 설정하지 않음
            watchHistoryDto.setEmail(Optional.empty());
        }

        watchHistoryService.createWatchHistory(watchHistoryDto, deviceUUID);
        return Response.success(new WatchHistoryResponse("시청기록 생성 성공"));
    }


}
