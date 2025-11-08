package com.uestc.scaffold.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    /* 成功状态码 */
    SUCCESS(200, "操作成功"),

    /* 客户端错误: 400-499 */
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "没有权限访问该资源"),
    NOT_FOUND(404, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    REQUEST_TIMEOUT(408, "请求超时"),
    CONFLICT(409, "资源冲突"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),

    /* 服务端错误: 500-599 */
    ERROR(500, "服务器内部错误"),
    NOT_IMPLEMENTED(501, "功能未实现"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),

    /* 业务错误: 1000-1999 */
    BUSINESS_ERROR(1000, "业务处理失败"),
    VALIDATION_ERROR(1001, "数据校验失败"),
    DUPLICATE_KEY(1002, "数据已存在"),
    DATABASE_ERROR(1003, "数据库操作失败"),

    /* 用户相关: 2000-2099 */
    USER_NOT_FOUND(2000, "用户不存在"),
    USER_ALREADY_EXISTS(2001, "用户已存在"),
    USER_DISABLED(2002, "用户已被禁用"),
    USERNAME_OR_PASSWORD_ERROR(2003, "用户名或密码错误"),
    USER_NOT_LOGIN(2004, "用户未登录"),
    LOGIN_EXPIRED(2005, "登录已过期"),

    /* 权限相关: 3000-3099 */
    PERMISSION_DENIED(3000, "权限不足"),
    ROLE_NOT_FOUND(3001, "角色不存在"),
    PERMISSION_NOT_FOUND(3002, "权限不存在"),

    /* Token相关: 4000-4099 */
    TOKEN_INVALID(4000, "Token无效"),
    TOKEN_EXPIRED(4001, "Token已过期"),
    TOKEN_MISSING(4002, "Token缺失"),

    /* 参数相关: 5000-5099 */
    PARAM_IS_BLANK(5000, "参数为空"),
    PARAM_TYPE_ERROR(5001, "参数类型错误"),
    PARAM_NOT_COMPLETE(5002, "参数缺失");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
