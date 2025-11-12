package com.iot.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iot.common.core.exception.BusinessException;
import com.iot.device.domain.Product;
import com.iot.device.mapper.DeviceMapper;
import com.iot.device.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 产品管理服务
 *
 * @author IoT Platform
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final DeviceMapper deviceMapper;

    /**
     * 分页查询产品列表
     */
    public IPage<Product> getProductPage(int pageNum, int pageSize, String keyword, String tenantId) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getTenantId, tenantId);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Product::getName, keyword)
                    .or()
                    .like(Product::getCode, keyword));
        }
        
        queryWrapper.orderByDesc(Product::getCreatedAt);
        return productMapper.selectPage(page, queryWrapper);
    }

    /**
     * 获取产品详情
     */
    public Product getProductById(String id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        
        // 统计设备数量
        Integer deviceCount = deviceMapper.countByProductId(id);
        product.setDeviceCount(deviceCount);
        
        return product;
    }

    /**
     * 创建产品
     */
    @Transactional(rollbackFor = Exception.class)
    public Product createProduct(Product product) {
        // 校验产品编码唯一性
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getCode, product.getCode());
        queryWrapper.eq(Product::getTenantId, product.getTenantId());
        if (productMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("产品编码已存在");
        }
        
        product.setId(UUID.randomUUID().toString().replace("-", ""));
        product.setStatus(1);
        product.setDeviceCount(0);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        productMapper.insert(product);
        log.info("创建产品成功，productId: {}, name: {}", product.getId(), product.getName());
        
        return product;
    }

    /**
     * 更新产品
     */
    @Transactional(rollbackFor = Exception.class)
    public Product updateProduct(Product product) {
        Product existProduct = productMapper.selectById(product.getId());
        if (existProduct == null) {
            throw new BusinessException("产品不存在");
        }
        
        // 校验产品编码唯一性（排除自己）
        if (StringUtils.hasText(product.getCode()) && !product.getCode().equals(existProduct.getCode())) {
            LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Product::getCode, product.getCode());
            queryWrapper.eq(Product::getTenantId, existProduct.getTenantId());
            queryWrapper.ne(Product::getId, product.getId());
            if (productMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("产品编码已存在");
            }
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        log.info("更新产品成功，productId: {}", product.getId());
        
        return productMapper.selectById(product.getId());
    }

    /**
     * 删除产品
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(String id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        
        // 检查是否有关联设备
        Integer deviceCount = deviceMapper.countByProductId(id);
        if (deviceCount > 0) {
            throw new BusinessException("该产品下还有设备，无法删除");
        }
        
        productMapper.deleteById(id);
        log.info("删除产品成功，productId: {}", id);
    }
}