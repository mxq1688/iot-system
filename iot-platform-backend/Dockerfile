# 多阶段构建：先编译，再运行
FROM maven:3.9-openjdk-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制项目文件
COPY . .

# 编译项目（跳过测试）
RUN mvn clean package -DskipTests

# 运行阶段：使用轻量级JRE镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 从构建阶段复制编译好的jar包
COPY --from=builder /app/iot-auth/target/*.jar app.jar

# 暴露端口
EXPOSE 8001

# 启动命令
CMD ["java", "-jar", "app.jar"]
