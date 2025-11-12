package com.iot.common.core.utils;

import java.util.UUID;

/**
 * ID生成工具类
 *
 * @author IoT Platform
 */
public class IdUtils {

    /**
     * 生成UUID（去掉-）
     */
    public static String fastUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成UUID（带-）
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成雪花ID（简化版，实际项目建议使用分布式ID生成器）
     */
    private static final long EPOCH = 1609459200000L; // 2021-01-01 00:00:00
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static long workerId = 1L;
    private static long datacenterId = 1L;
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

    /**
     * 生成雪花ID
     */
    public static synchronized long snowflakeId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 等待下一毫秒
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 生成字符串格式的雪花ID
     */
    public static String snowflakeIdStr() {
        return String.valueOf(snowflakeId());
    }
}