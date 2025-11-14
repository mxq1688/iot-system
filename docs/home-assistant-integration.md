# Home Assistant 接入指南

## 📋 目录

- [1. 接入方案概述](#1-接入方案概述)
- [2. MQTT Bridge方案（推荐）](#2-mqtt-bridge方案推荐)
- [3. RESTful API方案](#3-restful-api方案)
- [4. Webhook方案](#4-webhook方案)
- [5. 完整示例](#5-完整示例)

---

## 1. 接入方案概述

### 1.1 方案对比

| 接入方式 | 优点 | 缺点 | 适用场景 |
|---------|------|------|----------|
| **MQTT Bridge** | • 实时性最佳<br>• 双向通信<br>• 资源占用低<br>• 利用现有基础设施 | • 配置稍复杂 | • 实时监控<br>• 设备控制<br>• 状态同步 |
| **RESTful API** | • 实现简单<br>• 无需额外组件<br>• 易于调试 | • 轮询延迟<br>• 服务器压力大 | • 定时查询<br>• 手动触发 |
| **Webhook** | • 事件驱动<br>• 实时推送<br>• 单向高效 | • 仅支持推送<br>• 需公网访问 | • 告警通知<br>• 状态变化推送 |

### 1.2 推荐方案

✅ **MQTT Bridge** - 最适合您的系统架构

**理由：**
- 系统已部署 EMQX MQTT Broker (端口 1883)
- Home Assistant 原生支持 MQTT
- 实时双向通信，无轮询开销
- 可扩展性强，支持大量设备

---

## 2. MQTT Bridge方案（推荐）

### 2.1 架构图

```
┌─────────────────┐         MQTT          ┌─────────────────┐
│                 │    (tcp://host:1883)   │                 │
│  Home Assistant │◄──────────────────────►│  EMQX Broker    │
│                 │                        │                 │
└─────────────────┘                        └────────┬────────┘
                                                    │
                                                    │ MQTT
                                                    │
                                           ┌────────▼────────┐
                                           │                 │
                                           │  IoT Platform   │
                                           │  (Device Svc)   │
                                           │                 │
                                           └─────────────────┘
```

### 2.2 Topic 设计规范

#### 2.2.1 IoT平台 → Home Assistant (上报数据)

```yaml
# 设备状态上报
iot/device/{deviceId}/status
Payload: {"status": "online", "timestamp": 1699999999}

# 设备数据上报
iot/device/{deviceId}/data
Payload: {
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1699999999
}

# 设备告警上报
iot/device/{deviceId}/alarm
Payload: {
  "alarmType": "high_temperature",
  "level": "warning",
  "value": 35.5,
  "timestamp": 1699999999
}
```

#### 2.2.2 Home Assistant → IoT平台 (设备控制)

```yaml
# 设备控制命令
iot/device/{deviceId}/control
Payload: {
  "action": "switch",
  "params": {"power": "on"},
  "requestId": "req-123456"
}

# 场景触发
iot/scene/{sceneId}/trigger
Payload: {
  "sceneId": "scene-001",
  "userId": "user-001"
}
```

### 2.3 Home Assistant 配置

#### 2.3.1 配置 MQTT 集成

**方法1：通过 UI 配置**
1. 进入 Home Assistant
2. 设置 → 设备与服务 → 添加集成
3. 搜索 "MQTT" → 输入配置：
   - Broker: `你的服务器IP`
   - Port: `1883`
   - Username: `admin` (根据你的EMQX配置)
   - Password: `public` (根据你的EMQX配置)

**方法2：通过 configuration.yaml 配置**

编辑 `~/.homeassistant/configuration.yaml`：

```yaml
mqtt:
  broker: your-server-ip  # 替换为你的服务器IP
  port: 1883
  username: admin
  password: public
  discovery: true
  discovery_prefix: homeassistant
  birth_message:
    topic: 'homeassistant/status'
    payload: 'online'
  will_message:
    topic: 'homeassistant/status'
    payload: 'offline'
```

#### 2.3.2 定义 MQTT 传感器

在 `configuration.yaml` 中添加：

```yaml
sensor:
  # 温度传感器
  - platform: mqtt
    name: "IoT温度传感器"
    state_topic: "iot/device/sensor-001/data"
    value_template: "{{ value_json.temperature }}"
    unit_of_measurement: "°C"
    device_class: temperature
    unique_id: "iot_sensor_001_temp"
    
  # 湿度传感器
  - platform: mqtt
    name: "IoT湿度传感器"
    state_topic: "iot/device/sensor-001/data"
    value_template: "{{ value_json.humidity }}"
    unit_of_measurement: "%"
    device_class: humidity
    unique_id: "iot_sensor_001_humidity"

binary_sensor:
  # 设备在线状态
  - platform: mqtt
    name: "设备在线状态"
    state_topic: "iot/device/sensor-001/status"
    value_template: "{{ 'ON' if value_json.status == 'online' else 'OFF' }}"
    device_class: connectivity
    unique_id: "iot_sensor_001_status"

switch:
  # 开关控制
  - platform: mqtt
    name: "IoT智能开关"
    state_topic: "iot/device/switch-001/status"
    command_topic: "iot/device/switch-001/control"
    value_template: "{{ value_json.power }}"
    payload_on: '{"action":"switch","params":{"power":"on"}}'
    payload_off: '{"action":"switch","params":{"power":"off"}}'
    state_on: "on"
    state_off: "off"
    optimistic: false
    qos: 1
    retain: false
    unique_id: "iot_switch_001"
```

#### 2.3.3 定义自动化规则

```yaml
automation:
  # 温度过高告警
  - alias: "温度过高通知"
    trigger:
      - platform: mqtt
        topic: "iot/device/+/alarm"
    condition:
      - condition: template
        value_template: "{{ trigger.payload_json.alarmType == 'high_temperature' }}"
    action:
      - service: notify.mobile_app
        data:
          message: "设备{{ trigger.topic.split('/')[2] }}温度过高：{{ trigger.payload_json.value }}°C"
          title: "⚠️ 温度告警"

  # 设备离线通知
  - alias: "设备离线通知"
    trigger:
      - platform: mqtt
        topic: "iot/device/+/status"
    condition:
      - condition: template
        value_template: "{{ trigger.payload_json.status == 'offline' }}"
    action:
      - service: persistent_notification.create
        data:
          title: "设备离线"
          message: "设备 {{ trigger.topic.split('/')[2] }} 已离线"
```

### 2.4 IoT平台配置

#### 2.4.1 创建 Home Assistant 集成服务

创建新文件：`iot-device/src/main/java/com/iot/device/service/HomeAssistantService.java`

```java
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
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeAssistantService {

    private final MessageChannel mqttOutputChannel;

    /**
     * 发送设备状态到Home Assistant
     */
    public void publishDeviceStatus(String deviceId, String status) {
        String topic = String.format("iot/device/%s/status", deviceId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", status);
        payload.put("timestamp", System.currentTimeMillis() / 1000);
        
        publishMessage(topic, JSON.toJSONString(payload));
        log.info("发送设备状态到HA: deviceId={}, status={}", deviceId, status);
    }

    /**
     * 发送设备数据到Home Assistant
     */
    public void publishDeviceData(String deviceId, Map<String, Object> data) {
        String topic = String.format("iot/device/%s/data", deviceId);
        data.put("timestamp", System.currentTimeMillis() / 1000);
        
        publishMessage(topic, JSON.toJSONString(data));
        log.info("发送设备数据到HA: deviceId={}, data={}", deviceId, data);
    }

    /**
     * 发送设备告警到Home Assistant
     */
    public void publishDeviceAlarm(String deviceId, String alarmType, String level, Object value) {
        String topic = String.format("iot/device/%s/alarm", deviceId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("alarmType", alarmType);
        payload.put("level", level);
        payload.put("value", value);
        payload.put("timestamp", System.currentTimeMillis() / 1000);
        
        publishMessage(topic, JSON.toJSONString(payload));
        log.info("发送设备告警到HA: deviceId={}, alarm={}", deviceId, alarmType);
    }

    /**
     * 发送MQTT消息
     */
    private void publishMessage(String topic, String payload) {
        mqttOutputChannel.send(
            MessageBuilder
                .withPayload(payload)
                .setHeader("mqtt_topic", topic)
                .build()
        );
    }
}
```

#### 2.4.2 修改 MQTT 消息处理服务

编辑：`iot-device/src/main/java/com/iot/device/service/MqttMessageService.java`

在现有代码中添加 Home Assistant 控制命令处理：

```java
// 在 handleMessage 方法中添加
if (topic.contains("/control")) {
    handleControlCommand(deviceId, payload);
}

/**
 * 处理Home Assistant控制命令
 */
private void handleControlCommand(String deviceId, String payload) {
    try {
        JSONObject json = JSON.parseObject(payload);
        String action = json.getString("action");
        JSONObject params = json.getJSONObject("params");
        
        log.info("收到HA控制命令: deviceId={}, action={}, params={}", 
                 deviceId, action, params);
        
        // 根据action类型执行相应的设备控制
        switch (action) {
            case "switch":
                handleSwitchControl(deviceId, params);
                break;
            case "setValue":
                handleSetValue(deviceId, params);
                break;
            default:
                log.warn("未知的控制命令: {}", action);
        }
        
    } catch (Exception e) {
        log.error("处理HA控制命令失败: deviceId={}", deviceId, e);
    }
}
```

#### 2.4.3 更新 MQTT Topic 订阅

编辑：`iot-device/src/main/java/com/iot/device/config/MqttConfig.java`

```java
@Bean
public MqttPahoMessageDrivenChannelAdapter inbound() {
    MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
        clientId + "_inbound",
        mqttClientFactory(),
        "iot/device/+/data",      // 设备数据
        "iot/device/+/status",    // 设备状态  
        "iot/device/+/control",   // Home Assistant控制命令 (新增)
        "iot/scene/+/trigger"     // 场景触发 (新增)
    );
    // ... 其他配置
}
```

---

## 3. RESTful API方案

### 3.1 Home Assistant RESTful 集成

在 `configuration.yaml` 中配置：

```yaml
sensor:
  # 通过API获取设备数据
  - platform: rest
    name: "IoT设备温度"
    resource: "https://your-domain.com/api/v1/devices/sensor-001/data"
    method: GET
    headers:
      Authorization: "Bearer YOUR_JWT_TOKEN"
      Content-Type: "application/json"
    value_template: "{{ value_json.data.temperature }}"
    unit_of_measurement: "°C"
    scan_interval: 30  # 每30秒轮询一次

switch:
  # 通过API控制设备
  - platform: rest
    name: "IoT智能开关"
    resource: "https://your-domain.com/api/v1/devices/switch-001/control"
    state_resource: "https://your-domain.com/api/v1/devices/switch-001/status"
    body_on: '{"action":"switch","params":{"power":"on"}}'
    body_off: '{"action":"switch","params":{"power":"off"}}'
    is_on_template: "{{ value_json.data.power == 'on' }}"
    headers:
      Authorization: "Bearer YOUR_JWT_TOKEN"
      Content-Type: "application/json"
```

### 3.2 获取 JWT Token

在 Home Assistant 中添加 shell_command：

```yaml
shell_command:
  get_iot_token: |
    curl -X POST https://your-domain.com/auth/login \
      -H "Content-Type: application/json" \
      -d '{"username":"your_user","password":"your_password"}' \
      | jq -r '.data.token' > /config/iot_token.txt
```

---

## 4. Webhook方案

### 4.1 Home Assistant Webhook配置

```yaml
automation:
  - alias: "接收IoT平台告警"
    trigger:
      - platform: webhook
        webhook_id: iot_platform_alarm
    action:
      - service: notify.mobile_app
        data:
          message: "{{ trigger.json.message }}"
          title: "{{ trigger.json.title }}"
```

Webhook URL: `http://homeassistant.local:8123/api/webhook/iot_platform_alarm`

### 4.2 IoT平台Webhook发送

```java
@Service
public class HomeAssistantWebhookService {
    
    private final RestTemplate restTemplate;
    private final String webhookUrl = "http://homeassistant.local:8123/api/webhook";
    
    public void sendAlarm(String webhookId, String title, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", title);
        payload.put("message", message);
        payload.put("timestamp", System.currentTimeMillis());
        
        String url = webhookUrl + "/" + webhookId;
        restTemplate.postForEntity(url, payload, String.class);
    }
}
```

---

## 5. 完整示例

### 5.1 场景：智能温控系统

**需求：**
- IoT温度传感器实时上报数据到Home Assistant
- 当温度>30°C时，Home Assistant自动打开风扇
- 当温度<20°C时，Home Assistant自动关闭风扇

**Home Assistant配置：**

```yaml
# configuration.yaml

sensor:
  - platform: mqtt
    name: "客厅温度"
    state_topic: "iot/device/temp-001/data"
    value_template: "{{ value_json.temperature }}"
    unit_of_measurement: "°C"
    device_class: temperature

switch:
  - platform: mqtt
    name: "客厅风扇"
    state_topic: "iot/device/fan-001/status"
    command_topic: "iot/device/fan-001/control"
    payload_on: '{"action":"switch","params":{"power":"on"}}'
    payload_off: '{"action":"switch","params":{"power":"off"}}'
    state_on: "on"
    state_off: "off"

automation:
  - alias: "温度过高开启风扇"
    trigger:
      - platform: numeric_state
        entity_id: sensor.客厅温度
        above: 30
    action:
      - service: switch.turn_on
        target:
          entity_id: switch.客厅风扇
      - service: notify.mobile_app
        data:
          message: "温度过高({{ states('sensor.客厅温度') }}°C)，已自动开启风扇"

  - alias: "温度正常关闭风扇"
    trigger:
      - platform: numeric_state
        entity_id: sensor.客厅温度
        below: 20
    condition:
      - condition: state
        entity_id: switch.客厅风扇
        state: 'on'
    action:
      - service: switch.turn_off
        target:
          entity_id: switch.客厅风扇
```

**IoT平台代码：**

```java
// 温度传感器数据上报
@Scheduled(fixedRate = 10000) // 每10秒上报一次
public void reportTemperature() {
    String deviceId = "temp-001";
    double temperature = readTemperatureSensor();
    
    Map<String, Object> data = new HashMap<>();
    data.put("temperature", temperature);
    
    homeAssistantService.publishDeviceData(deviceId, data);
}

// 处理风扇控制命令
private void handleSwitchControl(String deviceId, JSONObject params) {
    String power = params.getString("power");
    
    // 控制实际硬件
    deviceControlService.controlDevice(deviceId, "power", power);
    
    // 反馈状态给Home Assistant
    homeAssistantService.publishDeviceStatus(deviceId, power);
}
```

### 5.2 测试步骤

1. **启动EMQX Broker**
```bash
cd iot-platform-backend
docker-compose up -d emqx
```

2. **配置Home Assistant MQTT**
- 添加MQTT集成
- 输入EMQX连接信息

3. **使用MQTT测试工具验证**
```bash
# 订阅测试
mosquitto_sub -h your-server-ip -t "iot/device/+/data" -v

# 发布测试
mosquitto_pub -h your-server-ip -t "iot/device/temp-001/data" \
  -m '{"temperature":25.5,"humidity":60}'
```

4. **查看Home Assistant实体**
- 开发者工具 → 状态
- 搜索 "sensor.iot温度传感器"
- 确认数据正常显示

---

## 6. 故障排查

### 6.1 常见问题

**问题1：Home Assistant连接不上MQTT**
```bash
# 检查EMQX是否运行
docker ps | grep emqx

# 检查端口是否开放
netstat -tuln | grep 1883

# 查看EMQX日志
docker logs -f emqx
```

**问题2：收不到设备数据**
- 检查Topic是否正确
- 确认MQTT QoS设置
- 查看Home Assistant日志：设置 → 系统 → 日志

**问题3：控制命令无效**
- 检查payload格式是否正确
- 确认IoT平台是否订阅了control topic
- 查看设备服务日志

### 6.2 调试工具

- **MQTT Explorer**: 可视化MQTT消息浏览器
- **MQTTX**: 跨平台MQTT客户端
- **Home Assistant Logs**: 实时日志查看

---

## 7. 性能优化建议

1. **合理设置上报频率**：避免过于频繁的数据上报
2. **使用retained消息**：保存最后状态，方便Home Assistant重启后恢复
3. **批量操作**：多个设备操作可以合并到一个场景中
4. **使用QoS 1**：保证消息至少送达一次
5. **清理会话**：定期清理不活跃的MQTT会话

---

## 8. 安全建议

1. **使用TLS加密**：EMQX启用SSL/TLS (端口8883)
2. **强密码策略**：MQTT用户使用强密码
3. **限制Topic权限**：配置EMQX ACL规则
4. **内网访问**：Home Assistant和EMQX在同一内网
5. **JWT Token有效期**：API方案使用短期token

---

## 9. 参考资料

- [Home Assistant MQTT Integration](https://www.home-assistant.io/integrations/mqtt/)
- [EMQX Documentation](https://www.emqx.io/docs/)
- [Home Assistant Automation](https://www.home-assistant.io/docs/automation/)
- [IoT平台开发计划](./development-plan.md)

---

**更新时间**: 2025-11-12  
**维护者**: IoT Platform Team
