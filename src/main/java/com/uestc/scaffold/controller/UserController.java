package com.uestc.scaffold.controller;

import com.uestc.scaffold.common.Result;
import com.uestc.scaffold.dto.UserRegisterDTO;
import com.uestc.scaffold.dto.UserResponseDTO;
import com.uestc.scaffold.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        log.info("用户注册请求: {}", registerDTO.getUsername());
        UserResponseDTO userResponseDTO = userService.register(registerDTO);
        return Result.success("注册成功", userResponseDTO);
    }
}
