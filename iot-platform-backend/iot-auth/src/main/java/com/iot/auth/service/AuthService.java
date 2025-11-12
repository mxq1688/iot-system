package com.iot.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iot.auth.domain.User;
import com.iot.auth.dto.LoginRequest;
import com.iot.auth.dto.LoginResponse;
import com.iot.auth.mapper.UserMapper;
import com.iot.common.core.exception.BusinessException;
import com.iot.common.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务
 *
 * @author IoT Platform
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request, String ipAddress) {
        // 1. 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        // 4. 生成Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("tenantId", user.getTenantId());
        
        String token = JwtUtils.createToken(user.getUsername(), claims);

        // 5. 更新用户登录信息
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setLastLoginTime(LocalDateTime.now());
        updateUser.setLastLoginIp(ipAddress);
        userMapper.updateById(updateUser);

        // 6. 构建响应
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .tenantId(user.getTenantId())
                .build();

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(7 * 24 * 3600L)
                .userInfo(userInfo)
                .build();
    }

    /**
     * 用户登出
     */
    public void logout(String userId) {
        log.info("用户登出成功，userId: {}", userId);
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        try {
            return JwtUtils.validateToken(token);
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 刷新Token
     */
    public String refreshToken(String oldToken) {
        if (!JwtUtils.validateToken(oldToken)) {
            throw new BusinessException("Token已失效");
        }
        return JwtUtils.refreshToken(oldToken);
    }
}