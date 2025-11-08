package com.uestc.scaffold.converter;

import com.uestc.scaffold.dto.UserResponseDTO;
import com.uestc.scaffold.entity.SysUser;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * 用户实体转换器
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserConverter {

    /**
     * 将SysUser实体转换为UserResponseDTO
     *
     * @param user 用户实体
     * @return UserResponseDTO
     */
    UserResponseDTO toResponseDTO(SysUser user);
}
