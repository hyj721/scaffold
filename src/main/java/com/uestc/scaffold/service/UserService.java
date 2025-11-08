package com.uestc.scaffold.service;

import com.uestc.scaffold.dto.UserRegisterDTO;
import com.uestc.scaffold.dto.UserResponseDTO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @return 注册成功的用户信息
     */
    UserResponseDTO register(UserRegisterDTO registerDTO);
}
