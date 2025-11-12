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

    // Topic格式：device/{deviceId}/data 或 device/{deviceId}/status
    private static final Pattern TOPIC_PATTERN = Pattern.compile("device/([^/]+)/(data|status)");

    /**
     * 处理MQTT消息
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        try {
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            String payload = new String((byte[]) message.getPayload());
            
            log.info("收到MQTT消息，topic: {}, payload: {}", topic, payload);
            
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