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
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

//    @PostMapping("/login")
//    public Response<LoginResponse> login(@RequestBody final UserDto userDto){
//         final LoginResponse response = userService.login(userDto);
//         return Response.success(response);
//    }

}
