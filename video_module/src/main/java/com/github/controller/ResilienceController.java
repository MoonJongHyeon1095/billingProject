package com.github.controller;

import com.github.common.response.Response;
import com.github.controller.response.ViewResponse;
import com.github.dto.ViewDto;
import com.github.service.ResilienceService;
import com.github.util.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/v1/video")
public class ResilienceController {
    private final ResilienceService resilienceService;
    /**
     *
     */
    @GetMapping("/error")
    public Response<ViewResponse> checkResilience() {

        resilienceService.case1();

        return Response.success();
    }
}
