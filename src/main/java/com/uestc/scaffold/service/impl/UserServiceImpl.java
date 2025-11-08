package com.uestc.scaffold.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uestc.scaffold.converter.UserConverter;
import com.uestc.scaffold.dto.UserRegisterDTO;
import com.uestc.scaffold.dto.UserResponseDTO;
import com.uestc.scaffold.entity.SysUser;
import com.uestc.scaffold.enums.UserFieldType;
import com.uestc.scaffold.mapper.SysUserMapper;
import com.uestc.scaffold.service.UserBloomFilterService;
import com.uestc.scaffold.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final UserBloomFilterService userBloomFilterService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDTO register(UserRegisterDTO registerDTO) {
        // 1. 检查所有字段是否已存在（使用布隆过滤器 + 数据库）
        for (UserFieldType fieldType : UserFieldType.values()) {
            checkFieldExists(fieldType, registerDTO);
        }

        // 2. 创建用户实体
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setStatus(1); // 默认启用
        user.setDeleted(0); // 默认未删除

        // 3. 保存用户
        userMapper.insert(user);
        log.info("用户注册成功: {}", user.getUsername());

        // 4. 将用户信息添加到布隆过滤器
        for (UserFieldType fieldType : UserFieldType.values()) {
            String value = fieldType.getValueFromEntity(user);
            userBloomFilterService.add(fieldType, value);
        }

        // 5. 转换为响应DTO返回（不包含密码等敏感信息）
        return userConverter.toResponseDTO(user);
    }

    /**
     * 检查字段是否已存在
     *
     * @param fieldType   字段类型
     * @param registerDTO 注册DTO
     */
    private void checkFieldExists(UserFieldType fieldType, UserRegisterDTO registerDTO) {
        String value = fieldType.getValueFromDTO(registerDTO);

        // 如果不需要检查（可选字段且为空），直接返回
        if (!fieldType.shouldCheck(value)) {
            return;
        }

        // 先用布隆过滤器快速判断
        if (userBloomFilterService.mightContain(fieldType, value)) {
            // 可能存在，查库确认（处理1%的误判）
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(fieldType.getEntityExtractor(), value);
            Long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                // 抛出异常时包含具体的字段值
                String errorMessage = String.format("%s: %s", fieldType.getExistsMessage(), value);
                throw new RuntimeException(errorMessage);
            }
        }
        // 布隆过滤器返回false，一定不存在，跳过数据库查询
    }
}
