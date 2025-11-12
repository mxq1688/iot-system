package com.iot.device.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iot.common.core.domain.Result;
import com.iot.device.domain.Device;
import com.iot.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备管理控制器
 *
 * @author IoT Platform
 */
@Slf4j
@Tag(name = "设备管理", description = "设备CRUD、状态管理、控制操作")
@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * 分页查询设备列表
     */
    @Operation(summary = "设备列表", description = "分页查询设备列表，支持关键字、产品、状态筛选")
    @GetMapping("/list")
    public Result<IPage<Device>> getDeviceList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) Integer status,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        
        IPage<Device> page = deviceService.getDevicePage(pageNum, pageSize, keyword, productId, status, tenantId);
        return Result.success(page);
    }

    /**
     * 获取设备详情
     */
    @Operation(summary = "设备详情", description = "根据ID获取设备详细信息")
    @GetMapping("/detail/{id}")
    public Result<Device> getDeviceDetail(@PathVariable String id) {
        Device device = deviceService.getDeviceById(id);
        return Result.success(device);
    }

    /**
     * 创建设备
     */
    @Operation(summary = "创建设备", description = "创建新设备")
    @PostMapping("/create")
    public Result<Device> createDevice(
            @RequestBody Device device,
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader("X-User-Id") String userId) {
        
        device.setTenantId(tenantId);
        device.setCreatedBy(userId);
        Device createdDevice = deviceService.createDevice(device);
        return Result.success("设备创建成功", createdDevice);
    }

    /**
     * 更新设备
     */
    @Operation(summary = "更新设备", description = "更新设备信息")
    @PutMapping("/update")
    public Result<Device> updateDevice(@RequestBody Device device) {
        Device updatedDevice = deviceService.updateDevice(device);
        return Result.success("设备更新成功", updatedDevice);
    }

    /**
     * 删除设备
     */
    @Operation(summary = "删除设备", description = "删除指定设备")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteDevice(@PathVariable String id) {
        deviceService.deleteDevice(id);
        return Result.success();
    }

    /**
     * 激活设备
     */
    @Operation(summary = "激活设备", description = "激活设备，使其可以正常使用")
    @PostMapping("/activate/{id}")
    public Result<Void> activateDevice(@PathVariable String id) {
        deviceService.activateDevice(id);
        return Result.success();
    }

    /**
     * 更新设备状态
     */
    @Operation(summary = "更新设备状态", description = "更新设备在线/离线状态")
    @PostMapping("/status")
    public Result<Void> updateDeviceStatus(
            @RequestParam String deviceId,
            @RequestParam Integer status) {
        
        deviceService.updateDeviceStatus(deviceId, status);
        return Result.success();
    }

    /**
     * 查询在线设备列表
     */
    @Operation(summary = "在线设备列表", description = "查询所有在线设备")
    @GetMapping("/online")
    public Result<List<Device>> getOnlineDevices(@RequestHeader("X-Tenant-Id") String tenantId) {
        List<Device> devices = deviceService.getOnlineDevices(tenantId);
        return Result.success(devices);
    }
}