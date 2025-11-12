package com.iot.device.service;

import com.alibaba.fastjson2.JSONObject;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * InfluxDB时序数据库服务
 *
 * @author IoT Platform
 */
@Slf4j
@Service
public class InfluxDbService {

    @Value("${influxdb.url}")
    private String url;

    @Value("${influxdb.token}")
    private String token;

    @Value("${influxdb.org}")
    private String organization;

    @Value("${influxdb.bucket}")
    private String bucket;

    private InfluxDBClient influxDBClient;
    private WriteApiBlocking writeApi;

    @PostConstruct
    public void init() {
        try {
            influxDBClient = InfluxDBClientFactory.create(url, token.toCharArray(), organization, bucket);
            writeApi = influxDBClient.getWriteApiBlocking();
            log.info("InfluxDB连接成功，url: {}, org: {}, bucket: {}", url, organization, bucket);
        } catch (Exception e) {
            log.error("InfluxDB连接失败", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (influxDBClient != null) {
            influxDBClient.close();
            log.info("InfluxDB连接已关闭");
        }
    }

    /**
     * 写入设备数据
     */
    public void writeDeviceData(String deviceId, JSONObject data) {
        try {
            Point point = Point.measurement("device_data")
                    .addTag("device_id", deviceId)
                    .time(Instant.now(), WritePrecision.MS);

            // 添加所有字段
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof Number) {
                    point.addField(key, ((Number) value).doubleValue());
                } else if (value instanceof Boolean) {
                    point.addField(key, (Boolean) value);
                } else {
                    point.addField(key, value.toString());
                }
            }

            writeApi.writePoint(point);
            log.debug("写入设备数据成功，deviceId: {}", deviceId);

        } catch (Exception e) {
            log.error("写入设备数据失败，deviceId: {}", deviceId, e);
        }
    }

    /**
     * 查询设备历史数据
     */
    public List<Map<String, Object>> queryDeviceData(String deviceId, String startTime, String endTime) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            String flux = String.format(
                    "from(bucket: \"%s\") "
                            + "|> range(start: %s, stop: %s) "
                            + "|> filter(fn: (r) => r._measurement == \"device_data\") "
                            + "|> filter(fn: (r) => r.device_id == \"%s\")",
                    bucket, startTime, endTime, deviceId
            );

            List<FluxTable> tables = influxDBClient.getQueryApi().query(flux, organization);

            for (FluxTable table : tables) {
                for (FluxRecord record : table.getRecords()) {
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("time", record.getTime());
                    dataPoint.put("field", record.getField());
                    dataPoint.put("value", record.getValue());
                    result.add(dataPoint);
                }
            }

            log.debug("查询设备数据成功，deviceId: {}, 记录数: {}", deviceId, result.size());

        } catch (Exception e) {
            log.error("查询设备数据失败，deviceId: {}", deviceId, e);
        }

        return result;
    }

    /**
     * 查询设备最新数据
     */
    public Map<String, Object> queryLatestDeviceData(String deviceId) {
        Map<String, Object> result = new HashMap<>();

        try {
            String flux = String.format(
                    "from(bucket: \"%s\") "
                            + "|> range(start: -1h) "
                            + "|> filter(fn: (r) => r._measurement == \"device_data\") "
                            + "|> filter(fn: (r) => r.device_id == \"%s\") "
                            + "|> last()",
                    bucket, deviceId
            );

            List<FluxTable> tables = influxDBClient.getQueryApi().query(flux, organization);

            for (FluxTable table : tables) {
                for (FluxRecord record : table.getRecords()) {
                    result.put(record.getField(), record.getValue());
                }
            }

            log.debug("查询设备最新数据成功，deviceId: {}", deviceId);

        } catch (Exception e) {
            log.error("查询设备最新数据失败，deviceId: {}", deviceId, e);
        }

        return result;
    }

    /**
     * 查询设备数据统计
     */
    public Map<String, Object> queryDeviceDataStatistics(String deviceId, String field, 
                                                          String startTime, String endTime, String windowPeriod) {
        Map<String, Object> result = new HashMap<>();

        try {
            String flux = String.format(
                    "from(bucket: \"%s\") "
                            + "|> range(start: %s, stop: %s) "
                            + "|> filter(fn: (r) => r._measurement == \"device_data\") "
                            + "|> filter(fn: (r) => r.device_id == \"%s\") "
                            + "|> filter(fn: (r) => r._field == \"%s\") "
                            + "|> aggregateWindow(every: %s, fn: mean, createEmpty: false)",
                    bucket, startTime, endTime, deviceId, field, windowPeriod
            );

            List<FluxTable> tables = influxDBClient.getQueryApi().query(flux, organization);
            List<Map<String, Object>> dataPoints = new ArrayList<>();

            for (FluxTable table : tables) {
                for (FluxRecord record : table.getRecords()) {
                    Map<String, Object> point = new HashMap<>();
                    point.put("time", record.getTime());
                    point.put("value", record.getValue());
                    dataPoints.add(point);
                }
            }

            result.put("data", dataPoints);
            result.put("count", dataPoints.size());

            log.debug("查询设备数据统计成功，deviceId: {}, field: {}", deviceId, field);

        } catch (Exception e) {
            log.error("查询设备数据统计失败，deviceId: {}", deviceId, e);
        }

        return result;
    }
}