package com.uestc.scaffold.enums;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.uestc.scaffold.dto.UserRegisterDTO;
import com.uestc.scaffold.entity.SysUser;
import lombok.Getter;

import java.util.function.Function;

/**
 * 用户字段类型枚举
 */
@Getter
public enum UserFieldType {

    /**
     * 用户名
     */
    USERNAME(
            "user:username:bloom",
            "用户名已存在",
            UserRegisterDTO::getUsername,
            SysUser::getUsername,
            true  // 必填
    ),

    /**
     * 邮箱
     */
    EMAIL(
            "user:email:bloom",
            "邮箱已被注册",
            UserRegisterDTO::getEmail,
            SysUser::getEmail,
            false // 可选
    ),

    /**
     * 手机号
     */
    PHONE(
            "user:phone:bloom",
            "手机号已被注册",
            UserRegisterDTO::getPhone,
            SysUser::getPhone,
            false // 可选
    );

    /**
     * 布隆过滤器的 Redis Key
     */
    private final String bloomFilterKey;

    /**
     * 字段已存在时的错误提示
     */
    private final String existsMessage;

    /**
     * 从 RegisterDTO 中提取字段值的函数
     */
    private final Function<UserRegisterDTO, String> dtoExtractor;

    /**
     * 从 SysUser 中提取字段值的函数（用于MyBatis-Plus查询）
     */
    private final SFunction<SysUser, String> entityExtractor;

    /**
     * 是否必填
     */
    private final boolean required;

    UserFieldType(String bloomFilterKey,
                  String existsMessage,
                  Function<UserRegisterDTO, String> dtoExtractor,
                  SFunction<SysUser, String> entityExtractor,
                  boolean required) {
        this.bloomFilterKey = bloomFilterKey;
        this.existsMessage = existsMessage;
        this.dtoExtractor = dtoExtractor;
        this.entityExtractor = entityExtractor;
        this.required = required;
    }

    /**
     * 从DTO中获取字段值
     */
    public String getValueFromDTO(UserRegisterDTO dto) {
        return dtoExtractor.apply(dto);
    }

    /**
     * 从实体中获取字段值
     */
    public String getValueFromEntity(SysUser user) {
        return entityExtractor.apply(user);
    }

    /**
     * 判断值是否为空
     */
    public boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * 判断是否需要检查（必填或有值）
     */
    public boolean shouldCheck(String value) {
        return required || !isEmpty(value);
    }
}
