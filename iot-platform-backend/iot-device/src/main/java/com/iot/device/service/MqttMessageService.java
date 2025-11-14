package com.iot.device.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MQTT消息处理服务
 *
 * @author IoT Platform
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MqttMessageService {

    private final DeviceService deviceService;
    private final InfluxDbService influxDbService;
    private final HomeAssistantService homeAssistantService;

    // Topic格式：device/{deviceId}/data 或 device/{deviceId}/status 或 device/{deviceId}/control
    private static final Pattern TOPIC_PATTERN = Pattern.compile("device/([^/]+)/(data|status|control)");
    // 场景触发格式：scene/{sceneId}/trigger
    private static final Pattern SCENE_PATTERN = Pattern.compile("scene/([^/]+)/trigger");

    /**
     * 处理MQTT消息
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        try {
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            String payload = new String((byte[]) message.getPayload());
            
            log.info("收到MQTT消息，topic: {}, payload: {}", topic, payload);
            
            // 检查是否为场景触发
            Matcher sceneMatcher = SCENE_PATTERN.matcher(topic);
            if (sceneMatcher.matches()) {
                String sceneId = sceneMatcher.group(1);
                handleSceneTrigger(sceneId, payload);
                return;
            }
            
            // 解析topic获取设备ID和消息类型
            Matcher matcher = TOPIC_PATTERN.matcher(topic);
            if (!matcher.matches()) {
                log.warn("无效的topic格式: {}", topic);
                return;
            }
            
            String deviceId = matcher.group(1);
            String messageType = matcher.group(2);
            
            // 根据消息类型分发处理
            if ("data".equals(messageType)) {
                handleDeviceData(deviceId, payload);
            } else if ("status".equals(messageType)) {
                handleDeviceStatus(deviceId, payload);
            } else if ("control".equals(messageType)) {
                handleControlCommand(deviceId, payload);
            }
            
        } catch (Exception e) {
            log.error("处理MQTT消息失败", e);
        }
    }

    /**
     * 处理设备数据上报
     */
    private void handleDeviceData(String deviceId, String payload) {
        try {
            JSONObject data = JSON.parseObject(payload);
            log.info("设备数据上报，deviceId: {}, data: {}", deviceId, data);
            
            // 存储到InfluxDB
            influxDbService.writeDeviceData(deviceId, data);
            
            // TODO: 触发规则引擎检查
            // TODO: 检查告警规则
            
        } catch (Exception e) {
            log.error("处理设备数据失败，deviceId: {}", deviceId, e);
        }
    }

    /**
     * 处理设备状态变化
     */
    private void handleDeviceStatus(String deviceId, String payload) {
        try {
            JSONObject statusData = JSON.parseObject(payload);
            Integer status = statusData.getInteger("status");
            
            if (status == null) {
                log.warn("设备状态消息缺少status字段，deviceId: {}", deviceId);
                return;
            }
            
            log.info("设备状态变化，deviceId: {}, status: {}", deviceId, status == 1 ? "在线" : "离线");
            
            // 更新设备状态
            deviceService.updateDeviceStatus(deviceId, status);
            
        } catch (Exception e) {
            log.error("处理设备状态失败，deviceId: {}", deviceId, e);
        }
    }

    /**
     * 处理Home Assistant控制命令
     */
    private void handleControlCommand(String deviceId, String payload) {
        try {
            JSONObject json = JSON.parseObject(payload);
            String action = json.getString("action");
            JSONObject params = json.getJSONObject("params");
            String requestId = json.getString("requestId");
            
            log.info("收到Home Assistant控制命令: deviceId={}, action={}, params={}, requestId={}",
                    deviceId, action, params, requestId);
            
            // 根据action类型执行相应的设备控制
            switch (action) {
                case "switch":
                    handleSwitchControl(deviceId, params);
                    break;
                case "setValue":
                    handleSetValue(deviceId, params);
                    break;
                case "adjustBrightness":
                    handleAdjustBrightness(deviceId, params);
                    break;
                case "setColor":
                    handleSetColor(deviceId, params);
                    break;
                default:
                    log.warn("未知的控制命令: action={}", action);
                    // 返回错误响应给Home Assistant
                    sendControlResponse(deviceId, requestId, false, "未知的控制命令: " + action);
                    return;
            }
            
            // 发送成功响应
            sendControlResponse(deviceId, requestId, true, "控制命令执行成功");
            
        } catch (Exception e) {
            log.error("处理Home Assistant控制命令失败: deviceId={}", deviceId, e);
        }
    }

    /**
     * 处理开关控制
     */
    private void handleSwitchControl(String deviceId, JSONObject params) {
        String power = params.getString("power");
        log.info("执行开关控制: deviceId={}, power={}", deviceId, power);
        
        // TODO: 调用实际的设备控制服务
        // deviceControlService.controlDevice(deviceId, "power", power);
        
        // 反馈状态给Home Assistant
        homeAssistantService.publishDeviceStatus(deviceId, power);
    }

    /**
     * 处理数值设置
     */
    private void handleSetValue(String deviceId, JSONObject params) {
        String attribute = params.getString("attribute");
        Object value = params.get("value");
        log.info("执行数值设置: deviceId={}, attribute={}, value={}", deviceId, attribute, value);
        
        // TODO: 调用实际的设备控制服务
        // deviceControlService.setAttribute(deviceId, attribute, value);
    }

    /**
     * 处理亮度调节
     */
    private void handleAdjustBrightness(String deviceId, JSONObject params) {
        Integer brightness = params.getInteger("brightness");
        log.info("执行亮度调节: deviceId={}, brightness={}", deviceId, brightness);
        
        // TODO: 调用实际的设备控制服务
        // deviceControlService.setBrightness(deviceId, brightness);
    }

    /**
     * 处理颜色设置
     */
    private void handleSetColor(String deviceId, JSONObject params) {
        String color = params.getString("color");
        log.info("执行颜色设置: deviceId={}, color={}", deviceId, color);
        
        // TODO: 调用实际的设备控制服务
        // deviceControlService.setColor(deviceId, color);
    }

    /**
     * 处理场景触发
     */
    private void handleSceneTrigger(String sceneId, String payload) {
        try {
            JSONObject json = JSON.parseObject(payload);
            String userId = json.getString("userId");
            
            log.info("收到场景触发请求: sceneId={}, userId={}", sceneId, userId);
            
            // TODO: 调用场景服务执行场景
            // sceneService.executeScene(sceneId, userId);
            
            // 发送执行结果给Home Assistant
            homeAssistantService.publishSceneResult(sceneId, true, "场景执行成功");
            
        } catch (Exception e) {
            log.error("处理场景触发失败: sceneId={}", sceneId, e);
            homeAssistantService.publishSceneResult(sceneId, false, "场景执行失败: " + e.getMessage());
        }
    }

    /**
     * 发送控制响应给Home Assistant
     */
    private void sendControlResponse(String deviceId, String requestId, boolean success, String message) {
        try {
            String topic = "iot/device/" + deviceId + "/response";
            JSONObject response = new JSONObject();
            response.put("requestId", requestId);
            response.put("success", success);
            response.put("message", message);
            response.put("timestamp", System.currentTimeMillis() / 1000);
            
            Message<String> mqttMessage = MessageBuilder
                    .withPayload(response.toJSONString())
                    .setHeader("mqtt_topic", topic)
                    .build();
            
            // 这里需要通过mqttOutputChannel发送
            log.info("发送控制响应: topic={}, response={}", topic, response);
            
        } catch (Exception e) {
            log.error("发送控制响应失败: deviceId={}, requestId={}", deviceId, requestId, e);
        }
    }

    /**
     * 发送控制指令到设备
     */
    public void sendControlCommand(String deviceId, JSONObject command) {
        try {
            String topic = "device/" + deviceId + "/control";
            String payload = command.toJSONString();
            
            log.info("发送控制指令，topic: {}, payload: {}", topic, payload);
            
            // 构建消息并发送
            Message<String> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader("mqtt_topic", topic)
                    .build();
            
            // 这里需要通过mqttOutputChannel发送
            // 实际发送逻辑在MqttConfig的outbound()方法中
            
        } catch (Exception e) {
            log.error("发送控制指令失败，deviceId: {}", deviceId, e);
        }
    }
}