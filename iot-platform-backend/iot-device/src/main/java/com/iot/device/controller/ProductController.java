package com.iot.device.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.iot.common.core.domain.Result;
import com.iot.device.domain.Product;
import com.iot.device.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 产品管理控制器
 *
 * @author IoT Platform
 */
@Slf4j
@Tag(name = "产品管理", description = "产品CRUD、查询统计")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 分页查询产品列表
     */
    @Operation(summary = "产品列表", description = "分页查询产品列表，支持关键字搜索")
    @GetMapping("/list")
    public Result<IPage<Product>> getProductList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestHeader("X-Tenant-Id") String tenantId) {
        
        IPage<Product> page = productService.getProductPage(pageNum, pageSize, keyword, tenantId);
        return Result.success(page);
    }

    /**
     * 获取产品详情
     */
    @Operation(summary = "产品详情", description = "根据ID获取产品详细信息")
    @GetMapping("/detail/{id}")
    public Result<Product> getProductDetail(@PathVariable String id) {
        Product product = productService.getProductById(id);
        return Result.success(product);
    }

    /**
     * 创建产品
     */
    @Operation(summary = "创建产品", description = "创建新的产品")
    @PostMapping("/create")
    public Result<Product> createProduct(
            @RequestBody Product product,
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader("X-User-Id") String userId) {
        
        product.setTenantId(tenantId);
        product.setCreatedBy(userId);
        Product createdProduct = productService.createProduct(product);
        return Result.success("产品创建成功", createdProduct);
    }

    /**
     * 更新产品
     */
    @Operation(summary = "更新产品", description = "更新产品信息")
    @PutMapping("/update")
    public Result<Product> updateProduct(@RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(product);
        return Result.success("产品更新成功", updatedProduct);
    }

    /**
     * 删除产品
     */
    @Operation(summary = "删除产品", description = "删除指定产品")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return Result.success();
    }
}