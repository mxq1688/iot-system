package com.iot.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 手动OpenAPI文档端点
 * 绕过springdoc自动配置的兼容性问题
 */
@RestController
@OpenAPIDefinition(
    info = @Info(
        title = "IoT Auth Service API",
        version = "1.0.0",
        description = "认证授权服务接口文档"
    )
)
public class OpenApiController {

    @GetMapping("/v3/api-docs")
    public Map<String, Object> getApiDocs() {
        Map<String, Object> openApi = new HashMap<>();
        openApi.put("openapi", "3.0.1");
        
        Map<String, Object> info = new HashMap<>();
        info.put("title", "IoT Auth Service API");
        info.put("description", "认证授权服务接口文档");
        info.put("version", "1.0.0");
        openApi.put("info", info);
        
        Map<String, Object> paths = new HashMap<>();
        
        // /auth/login endpoint
        Map<String, Object> loginPath = new HashMap<>();
        Map<String, Object> loginPost = new HashMap<>();
        loginPost.put("summary", "用户登录");
        loginPost.put("description", "用户登录接口");
        loginPost.put("tags", new String[]{"认证管理"});
        
        Map<String, Object> loginRequestBody = new HashMap<>();
        Map<String, Object> loginContent = new HashMap<>();
        Map<String, Object> loginJsonSchema = new HashMap<>();
        Map<String, Object> loginSchema = new HashMap<>();
        loginSchema.put("type", "object");
        Map<String, Object> loginProperties = new HashMap<>();
        
        Map<String, String> usernameField = new HashMap<>();
        usernameField.put("type", "string");
        usernameField.put("description", "用户名");
        loginProperties.put("username", usernameField);
        
        Map<String, String> passwordField = new HashMap<>();
        passwordField.put("type", "string");
        passwordField.put("description", "密码");
        loginProperties.put("password", passwordField);
        
        loginSchema.put("properties", loginProperties);
        loginJsonSchema.put("schema", loginSchema);
        loginContent.put("application/json", loginJsonSchema);
        loginRequestBody.put("content", loginContent);
        loginPost.put("requestBody", loginRequestBody);
        
        Map<String, Object> loginResponses = new HashMap<>();
        Map<String, Object> login200 = new HashMap<>();
        login200.put("description", "登录成功");
        loginResponses.put("200", login200);
        loginPost.put("responses", loginResponses);
        
        loginPath.put("post", loginPost);
        paths.put("/auth/login", loginPath);
        
        // /auth/register endpoint
        Map<String, Object> registerPath = new HashMap<>();
        Map<String, Object> registerPost = new HashMap<>();
        registerPost.put("summary", "用户注册");
        registerPost.put("description", "用户注册接口");
        registerPost.put("tags", new String[]{"认证管理"});
        registerPath.put("post", registerPost);
        paths.put("/auth/register", registerPath);
        
        // /auth/logout endpoint
        Map<String, Object> logoutPath = new HashMap<>();
        Map<String, Object> logoutPost = new HashMap<>();
        logoutPost.put("summary", "用户登出");
        logoutPost.put("description", "用户登出接口");
        logoutPost.put("tags", new String[]{"认证管理"});
        logoutPath.put("post", logoutPost);
        paths.put("/auth/logout", logoutPath);
        
        openApi.put("paths", paths);
        
        return openApi;
    }
    
    @GetMapping("/swagger-ui.html")
    public String getSwaggerUi() {
        return "<!DOCTYPE html>\n" +
                "<html lang='zh-CN'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <title>IoT Auth Service API</title>\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 50px; }\n" +
                "        h1 { color: #333; }\n" +
                "        .info { background: #f0f0f0; padding: 20px; border-radius: 5px; }\n" +
                "        .link { margin: 10px 0; }\n" +
                "        a { color: #007bff; text-decoration: none; }\n" +
                "        a:hover { text-decoration: underline; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>IoT Auth Service API Documentation</h1>\n" +
                "    <div class='info'>\n" +
                "        <h2>API信息</h2>\n" +
                "        <p><strong>服务名称:</strong> IoT Auth Service</p>\n" +
                "        <p><strong>版本:</strong> 1.0.0</p>\n" +
                "        <p><strong>描述:</strong> 认证授权服务接口文档</p>\n" +
                "        <div class='link'>\n" +
                "            <a href='/v3/api-docs' target='_blank'>查看OpenAPI JSON文档</a>\n" +
                "        </div>\n" +
                "        <h3>可用的API端点:</h3>\n" +
                "        <ul>\n" +
                "            <li>POST /auth/login - 用户登录</li>\n" +
                "            <li>POST /auth/register - 用户注册</li>\n" +
                "            <li>POST /auth/logout - 用户登出</li>\n" +
                "        </ul>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
