package com.github.controller;

import com.github.service.ResilienceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/v1/video")
public class ResilienceController {
    private final ResilienceService resilienceService;

    @GetMapping("/error")
    public ResponseEntity checkResilience() {

        return resilienceService.case1();

    }
}
