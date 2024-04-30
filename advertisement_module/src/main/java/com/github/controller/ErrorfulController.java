package com.github.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.Random;

@Slf4j
@RequestMapping("/v1/advertisement")
@RestController
public class ErrorfulController {

    @GetMapping("/error/case1")
    public ResponseEntity<String> case1() {
//        // Simulate 5% chance of 500 error
//        if (new Random().nextInt(100) < 5) {
//            return ResponseEntity.status(500).body("Internal Server Error");
//        }
//        return ResponseEntity.ok("Normal response");
        log.info("111111");
        //100%
        return ResponseEntity.status(500).body("Internal Server Error");
    }

    @GetMapping("/error/case2")
    public ResponseEntity<String> case2() {
        // Simulate blocking requests every first 10 seconds
        LocalTime currentTime = LocalTime.now();
        int currentSecond = currentTime.getSecond();

        if (currentSecond < 10) {
            // Simulate a delay (block) for 10 seconds
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return ResponseEntity.status(503).body("Service Unavailable");
        }

        return ResponseEntity.ok("Normal response");
    }

    @GetMapping("/error/case3")
    public ResponseEntity<String> case3() {
        // Simulate 500 error every first 10 seconds
        LocalTime currentTime = LocalTime.now();
        int currentSecond = currentTime.getSecond();

        if (currentSecond < 10) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }

        return ResponseEntity.ok("Normal response");
    }
}
