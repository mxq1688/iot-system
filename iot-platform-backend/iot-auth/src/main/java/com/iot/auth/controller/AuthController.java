package com.iot.auth.controller;

import com.iot.auth.dto.LoginRequest;
import com.iot.auth.dto.LoginResponse;
import com.iot.auth.service.AuthService;
import com.iot.common.core.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证授权控制器
 *
 * @author IoT Platform
 */
@Slf4j
@Tag(name = "认证授权", description = "用户登录、登出、Token管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "使用用户名和密码登录系统")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request, 
                                       HttpServletRequest httpRequest) {
        log.info("用户登录请求，username: {}", request.getUsername());
        
        String ipAddress = getIpAddress(httpRequest);
        LoginResponse response = authService.login(request, ipAddress);
        
        log.info("用户登录成功，username: {}, userId: {}", request.getUsername(), response.getUserInfo().getUserId());
        return Result.success(response);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出", description = "退出登录并清除Token")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestParam String userId) {
        log.info("用户登出请求，userId: {}", userId);
        authService.logout(userId);
        return Result.success();
    }

    /**
     * 验证Token
     */
    @Operation(summary = "验证Token", description = "验证Token是否有效")
    @GetMapping("/validate")
    public Result<Boolean> validateToken(@RequestParam String token) {
        boolean valid = authService.validateToken(token);
        return Result.success(valid);
    }

    /**
     * 刷新Token
     */
    @Operation(summary = "刷新Token", description = "使用旧Token获取新Token")
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestParam String token) {
        String newToken = authService.refreshToken(token);
        return Result.success("Token刷新成功", newToken);
    }

    /**
     * 健康检查
     */
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Auth Service is running");
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}