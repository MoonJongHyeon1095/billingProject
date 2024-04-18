package com.github.controller;

import com.github.common.response.Response;
import com.github.controller.response.LoginResponse;
import com.github.dto.UserDto;
import com.github.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public Response signup(@RequestBody UserDto userDto){
        System.out.println("11111 " + userDto.getEmail());
        userService.signup(userDto);
        return Response.success();
    }

    @PostMapping("/login")
    public Response<LoginResponse> login(@RequestBody UserDto userDto){
         final LoginResponse response = userService.login(userDto);
         return Response.success(response);
    }

}
