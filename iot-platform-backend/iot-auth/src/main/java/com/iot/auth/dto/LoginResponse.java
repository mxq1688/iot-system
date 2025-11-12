package com.iot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录响应
 *
 * @author IoT Platform
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String userId;
        private String username;
        private String realName;
        private String email;
        private String phone;
        private String avatar;
        private String tenantId;
    }
}