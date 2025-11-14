package com.iot.device.service;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Home Assistant 集成服务
 * 提供与Home Assistant的MQTT通信功能
 *
 * @author IoT Platform
 * @date 2025-11-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeAssistantService {

    private final MessageChannel mqttOutputChannel;

    /**
     * 发送设备状态到Home Assistant
     *
     * @param deviceId 设备ID
     * @param status   状态 (online/offline)
     */
    public void publishDeviceStatus(String deviceId, String status) {
        String topic = String.format("iot/device/%s/status", deviceId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", status);
        payload.put("timestamp", System.currentTimeMillis() / 1000);

        publishMessage(topic, JSON.toJSONString(payload));
        log.info("发送设备状态到Home Assistant: deviceId={}, status={}", deviceId, status);
    }

    /**
     * 发送设备数据到Home Assistant
     *
     * @param deviceId 设备ID
     * @param data     设备数据 (温度、湿度等)
     */
    public void publishDeviceData(String deviceId, Map<String, Object> data) {
        String topic = String.format("iot/device/%s/data", deviceId);
        data.put("timestamp", System.currentTimeMillis() / 1000);

        publishMessage(topic, JSON.toJSONString(data));
        log.info("发送设备数据到Home Assistant: deviceId={}, data={}", deviceId, data);
    }

    /**
     * 发送设备告警到Home Assistant
     *
     * @param deviceId  设备ID
     * @param alarmType 告警类型
     * @param level     告警级别 (info/warning/error/critical)
     * @param value     告警值
     */
    public void publishDeviceAlarm(String deviceId, String alarmType, String level, Object value) {
        String topic = String.format("iot/device/%s/alarm", deviceId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("alarmType", alarmType);
        payload.put("level", level);
        payload.put("value", value);
        payload.put("timestamp", System.currentTimeMillis() / 1000);

        publishMessage(topic, JSON.toJSONString(payload));
        log.info("发送设备告警到Home Assistant: deviceId={}, alarmType={}, level={}, value={}",
                deviceId, alarmType, level, value);
    }

    /**
     * 发送场景执行结果到Home Assistant
     *
     * @param sceneId 场景ID
     * @param success 是否成功
     * @param message 结果消息
     */
    public void publishSceneResult(String sceneId, boolean success, String message) {
        String topic = String.format("iot/scene/%s/result", sceneId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("success", success);
        payload.put("message", message);
        payload.put("timestamp", System.currentTimeMillis() / 1000);

        publishMessage(topic, JSON.toJSONString(payload));
        log.info("发送场景执行结果到Home Assistant: sceneId={}, success={}, message={}",
                sceneId, success, message);
    }

    /**
     * 批量发送设备数据
     *
     * @param devices 设备数据列表 (key: deviceId, value: data)
     */
    public void publishBatchDeviceData(Map<String, Map<String, Object>> devices) {
        devices.forEach((deviceId, data) -> {
            try {
                publishDeviceData(deviceId, data);
            } catch (Exception e) {
                log.error("批量发送设备数据失败: deviceId={}", deviceId, e);
            }
        });
        log.info("批量发送设备数据完成: 总数={}", devices.size());
    }

    /**
     * 发送MQTT消息的通用方法
     *
     * @param topic   MQTT主题
     * @param payload JSON格式的消息内容
     */
    private void publishMessage(String topic, String payload) {
        try {
            mqttOutputChannel.send(
                    MessageBuilder
                            .withPayload(payload)
                            .setHeader("mqtt_topic", topic)
                            .setHeader("mqtt_retained", false)  // 不保留消息
                            .setHeader("mqtt_qos", 1)           // QoS 1: 至少一次
                            .build()
            );
            log.debug("MQTT消息发送成功: topic={}, payload={}", topic, payload);
        } catch (Exception e) {
            log.error("MQTT消息发送失败: topic={}, payload={}", topic, payload, e);
            throw new RuntimeException("发送MQTT消息失败", e);
        }
    }

    /**
     * 发送Home Assistant Discovery配置
     * 用于自动发现设备
     *
     * @param deviceId     设备ID
     * @param deviceName   设备名称
     * @param deviceType   设备类型 (sensor/switch/light等)
     * @param config       设备配置
     */
    public void publishDiscoveryConfig(String deviceId, String deviceName,
                                        String deviceType, Map<String, Object> config) {
        String topic = String.format("homeassistant/%s/%s/config", deviceType, deviceId);

        Map<String, Object> discoveryConfig = new HashMap<>();
        discoveryConfig.put("unique_id", deviceId);
        discoveryConfig.put("name", deviceName);
        discoveryConfig.put("state_topic", String.format("iot/device/%s/data", deviceId));
        discoveryConfig.putAll(config);

        publishMessage(topic, JSON.toJSONString(discoveryConfig));
        log.info("发送Home Assistant Discovery配置: deviceId={}, type={}", deviceId, deviceType);
    }
}
