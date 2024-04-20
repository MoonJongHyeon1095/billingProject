package com.github.controller;

import com.github.common.response.Response;
import com.github.controller.response.LoginResponse;
import com.github.controller.response.RefreshResponse;
import com.github.controller.response.SignupResponse;
import com.github.dto.RefreshDto;
import com.github.dto.UserDto;
import com.github.service.UserService;
import com.github.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public Response<SignupResponse> signup(@RequestBody final UserDto userDto){
        final SignupResponse response = userService.signup(userDto);
        return Response.success(response);
    }

    @PostMapping("/login")
    public Response<LoginResponse> login(@RequestBody final UserDto userDto){
         final LoginResponse response = userService.login(userDto);
         return Response.success(response);
    }

    @PostMapping("/refresh")
    public Response<RefreshResponse> refresh(@RequestBody final RefreshDto refreshDto){
        final RefreshResponse response = authService.refresh(refreshDto);
        return Response.success(response);
    }

}
