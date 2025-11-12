package com.iot.device.controller;

import com.iot.common.core.domain.Result;
import com.iot.device.service.InfluxDbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 设备数据查询控制器
 *
 * @author IoT Platform
 */
@Slf4j
@Tag(name = "设备数据", description = "设备历史数据查询、统计分析")
@RestController
@RequestMapping("/device/data")
@RequiredArgsConstructor
public class DeviceDataController {

    private final InfluxDbService influxDbService;

    /**
     * 查询设备历史数据
     */
    @Operation(summary = "历史数据", description = "查询设备在指定时间范围内的历史数据")
    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getDeviceHistory(
            @RequestParam String deviceId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        
        List<Map<String, Object>> data = influxDbService.queryDeviceData(deviceId, startTime, endTime);
        return Result.success(data);
    }

    /**
     * 查询设备最新数据
     */
    @Operation(summary = "最新数据", description = "查询设备最新上报的数据")
    @GetMapping("/latest")
    public Result<Map<String, Object>> getLatestData(@RequestParam String deviceId) {
        Map<String, Object> data = influxDbService.queryLatestDeviceData(deviceId);
        return Result.success(data);
    }

    /**
     * 查询设备数据统计
     */
    @Operation(summary = "数据统计", description = "查询设备数据的统计信息（平均值、聚合等）")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getDeviceStatistics(
            @RequestParam String deviceId,
            @RequestParam String field,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(defaultValue = "1h") String windowPeriod) {
        
        Map<String, Object> statistics = influxDbService.queryDeviceDataStatistics(
                deviceId, field, startTime, endTime, windowPeriod);
        return Result.success(statistics);
    }
}