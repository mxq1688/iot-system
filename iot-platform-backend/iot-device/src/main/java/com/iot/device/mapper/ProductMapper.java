package com.iot.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.device.domain.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品Mapper接口
 *
 * @author IoT Platform
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}