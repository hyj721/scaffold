package com.uestc.scaffold.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 请求日志拦截器
 * 功能：
 * 1. 为每个请求生成唯一的 traceId
 * 2. 记录请求的基本信息（方法、路径、IP、处理器）
 * 3. 计算请求处理耗时
 * 4. 记录响应状态
 */
@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final String TRACE_ID = "traceId";
    private static final String START_TIME = "startTime";

    /**
     * 请求处理前执行
     * 记录请求开始时间，生成 traceId
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 生成唯一的 traceId
        String traceId = UUID.randomUUID().toString().replace("-", "");
        MDC.put(TRACE_ID, traceId);

        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME, startTime);

        // 获取请求信息
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = getClientIp(request);
        String handlerInfo = getHandlerInfo(handler);

        // 记录请求开始日志
        log.info("请求开始 >>> {} {} | IP: {} | Handler: {}",
                method, uri, ip, handlerInfo);

        return true;
    }

    /**
     * 请求处理后执行（视图渲染前）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                          Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        // 这里可以记录一些中间状态，如果需要的话
    }

    /**
     * 请求完成后执行（视图渲染后或异常发生后）
     * 计算耗时，记录完成日志
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, @Nullable Exception ex) throws Exception {
        try {
            // 计算请求耗时
            Long startTime = (Long) request.getAttribute(START_TIME);
            long duration = System.currentTimeMillis() - startTime;

            // 获取响应状态
            int status = response.getStatus();
            String method = request.getMethod();
            String uri = request.getRequestURI();

            // 判断是否有异常
            if (ex != null) {
                log.error("请求异常 <<< {} {} | 状态: {} | 耗时: {}ms | 异常: {}",
                        method, uri, status, duration, ex.getMessage(), ex);
            } else if (status >= 400) {
                log.warn("请求失败 <<< {} {} | 状态: {} | 耗时: {}ms",
                        method, uri, status, duration);
            } else {
                log.info("请求完成 <<< {} {} | 状态: {} | 耗时: {}ms",
                        method, uri, status, duration);
            }
        } finally {
            // 清理 MDC，避免内存泄漏
            MDC.clear();
        }
    }

    /**
     * 获取客户端真实 IP
     * 处理代理、负载均衡等情况
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个 IP 的情况（取第一个）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 获取处理器信息
     * 如果是 Controller 方法，返回类名和方法名
     */
    private String getHandlerInfo(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String className = handlerMethod.getBeanType().getSimpleName();
            String methodName = handlerMethod.getMethod().getName();
            return className + "." + methodName + "()";
        }
        return handler.getClass().getSimpleName();
    }
}
