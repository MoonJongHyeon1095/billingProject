package com.github.controller;

import com.github.common.response.Response;
import com.github.controller.response.ViewResponse;
import com.github.dto.ViewDto;
import com.github.service.ViewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/v1/play")
public class VideoController {
    private final ViewService viewService;

    /**
     * 영상 조회수 증가 및 가장 최근의 시청중단 지점 응답
     * @param viewDto
     * @param deviceUUID 로그인을 하지 않은 유저를 식별하는 데에 사용
     * @return
     */
    @PostMapping
    public Response<ViewResponse> countView(
            @RequestBody final ViewDto viewDto,
            @RequestHeader("X-User-Email") final String email,
            @RequestHeader("X-Device-UUID") final String deviceUUID
    ) {
        if (email!=null) {
          viewDto.setEmail(Optional.of(email));
        } else {
            // 로그인하지 않은 사용자일 경우, email 설정하지 않음
            viewDto.setEmail(Optional.empty());
        }

        viewService.countView(viewDto);
        ViewResponse response = viewService.getLastWatched(viewDto, deviceUUID);
        return Response.success(response);
    }


}
