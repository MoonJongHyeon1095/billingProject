package com.github.controller;

import com.github.common.response.Response;
import com.github.controller.response.VideoResponse;
import com.github.controller.response.ViewResponse;
import com.github.dto.WatchHistoryDto;
import com.github.dto.ViewDto;
import com.github.service.ViewService;
import com.github.service.WatchHistoryService;
import com.github.util.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            if (userDetails.getUserId() != null) viewDto.setUserId(Optional.of(userDetails.getUserId()));

        } else {
            // 로그인하지 않은 사용자일 경우, userId를 설정하지 않음
            viewDto.setUserId(Optional.empty());
        }

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

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            if (userDetails.getUserId() != null) watchHistoryDto.setUserId(Optional.of(userDetails.getUserId()));

        } else {
            // 로그인하지 않은 사용자일 경우, userId를 설정하지 않음
            watchHistoryDto.setUserId(Optional.empty());
        }

        watchHistoryService.createWatchHistory(watchHistoryDto, deviceUUID);
        return Response.success(new VideoResponse("시청기록 생성 성공"));
    }


}
