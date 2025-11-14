package com.iot.device.config;

import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * MQTT配置
 *
 * @author IoT Platform
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mqtt.broker")
public class MqttConfig {

    private String url;
    private String username;
    private String password;
    private String clientId;

    /**
     * MQTT客户端工厂
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{url});
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        options.setKeepAliveInterval(60);
        options.setConnectionTimeout(30);
        options.setAutomaticReconnect(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    /**
     * MQTT输入通道
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT输出通道
     */
    @Bean
    public MessageChannel mqttOutputChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT消息接收适配器
     */
    @Bean
    public MqttPahoMessageDrivenChannelAdapter inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                clientId + "_inbound",
                mqttClientFactory(),
                "device/+/data",      // 设备数据主题
                "device/+/status",    // 设备状态主题
                "iot/device/+/control",  // Home Assistant控制命令 (新增)
                "iot/scene/+/trigger"    // Home Assistant场景触发 (新增)
        );
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    /**
     * MQTT消息发送处理器
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutputChannel")
    public MessageHandler outbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                clientId + "_outbound",
                mqttClientFactory()
        );
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(1);
        messageHandler.setDefaultRetained(false);
        return messageHandler;
    }
}