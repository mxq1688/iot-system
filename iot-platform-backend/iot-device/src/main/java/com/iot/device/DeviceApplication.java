package com.iot.device;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 设备管理服务启动类
 *
 * @author IoT Platform
 */
@SpringBootApplication(scanBasePackages = {"com.iot"})
@EnableDiscoveryClient
@MapperScan("com.iot.device.mapper")
public class DeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceApplication.class, args);
        System.out.println("");
        System.out.println("   _____ ____  ______   ____             _          ");
        System.out.println("  /  _// __ \\/_  __/  / __ \\___  _   __(_)_______  ");
        System.out.println("  / / / / / / / /    / / / / _ \\| | / / / ___/ _ \\ ");
        System.out.println("_/ / / /_/ / / /    / /_/ /  __/| |/ / / /__/  __/ ");
        System.out.println("/___/\\____/ /_/    /_____/\\___/ |___/_/\\___/\\___/  ");
        System.out.println("");
        System.out.println("IoT Device Service Started Successfully!");
        System.out.println("");
    }
}