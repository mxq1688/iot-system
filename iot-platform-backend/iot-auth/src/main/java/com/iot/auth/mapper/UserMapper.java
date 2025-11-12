package com.iot.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.auth.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * @author IoT Platform
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}