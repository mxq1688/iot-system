package com.iot.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证授权服务启动类
 *
 * @author IoT Platform
 */
@SpringBootApplication(scanBasePackages = {"com.iot"})
// @EnableDiscoveryClient  // Disabled for standalone mode
@MapperScan("com.iot.auth.mapper")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("");
        System.out.println("   _____ ____  ______   ___         __  __     ");
        System.out.println("  /  _// __ \\/_  __/  /   | __  __/ /_/ /_    ");
        System.out.println("  / / / / / / / /    / /| |/ / / / __/ __ \\   ");
        System.out.println("_/ / / /_/ / / /    / ___ / /_/ / /_/ / / /   ");
        System.out.println("/___/\\____/ /_/    /_/  |_\\__,_/\\__/_/ /_/    ");
        System.out.println("");
        System.out.println("IoT Auth Service Started Successfully!");
        System.out.println("");
    }
}