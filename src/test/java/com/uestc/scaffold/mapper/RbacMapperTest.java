package com.uestc.scaffold.mapper;

import com.uestc.scaffold.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RBAC实体插入测试
 */
@SpringBootTest
public class RbacMapperTest {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    /**
     * 测试完整的RBAC数据插入流程
     */
    @Test
    public void testInsertCompleteRbac() {
        // 1. 创建用户
        SysUser user = new SysUser();
        user.setUsername("zhangsan");
        user.setPassword("123456");
        user.setNickname("张三");
        user.setEmail("zhangsan@example.com");
        user.setStatus(1);
        user.setDeleted(0);
        sysUserMapper.insert(user);
        System.out.println("创建用户成功，ID: " + user.getId());

        // 2. 创建角色
        SysRole role = new SysRole();
        role.setRoleName("编辑");
        role.setRoleCode("EDITOR");
        role.setDescription("内容编辑人员");
        role.setStatus(1);
        role.setDeleted(0);
        sysRoleMapper.insert(role);
        System.out.println("创建角色成功，ID: " + role.getId());

        // 3. 创建权限
        SysPermission permission = new SysPermission();
        permission.setPermissionName("文章管理");
        permission.setPermissionCode("sys:article:manage");
        permission.setType(1);
        permission.setStatus(1);
        permission.setDeleted(0);
        sysPermissionMapper.insert(permission);
        System.out.println("创建权限成功，ID: " + permission.getId());

        // 4. 用户绑定角色
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        sysUserRoleMapper.insert(userRole);
        System.out.println("用户绑定角色成功");

        // 5. 角色绑定权限
        SysRolePermission rolePermission = new SysRolePermission();
        rolePermission.setRoleId(role.getId());
        rolePermission.setPermissionId(permission.getId());
        sysRolePermissionMapper.insert(rolePermission);
        System.out.println("角色绑定权限成功");

        // 验证
        assertNotNull(user.getId());
        assertNotNull(role.getId());
        assertNotNull(permission.getId());
        assertNotNull(userRole.getId());
        assertNotNull(rolePermission.getId());

        System.out.println("完整RBAC数据插入测试通过！");
    }

    /**
     * 更新user信息
     */
    @Test
    public void testUpdateUser() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setNickname("李四");
        sysUserMapper.updateById(user);
    }
}