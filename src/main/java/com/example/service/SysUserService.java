package com.example.service;

import com.example.entity.SysUser;
import com.example.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheManager = "cacheManager")
public class SysUserService {
    @Autowired
    private SysUserMapper userMapper;
    @Cacheable(cacheNames = "sysUser",key = "#id",condition = "#id>0",unless ="#result==null")
    public SysUser selectById(Integer id) {
        return userMapper.selectById(id);
    }
   /* @Caching(
            cacheable = {
                    @Cacheable(value = "sysUser",key = "#name")
            },
            put = {
                    @CachePut(value = "sysUser",key = "#result.id"),
                    @CachePut(value = "sysUser",key = "#result.password")
            }
    )*/
    public SysUser selectByName(String name) {
        return userMapper.selectByName(name);
    }

    /**
     * @CachePut：既调用方法，又更新缓存；
     * 修改了数据库的某个数据，同时又更新缓存
     */

    @CachePut(cacheNames= "sysUser",key = "#id")
    public Integer updateById (int id){
        userMapper.updateById(id);
        return null;
    }
    /**
     * 清除缓存
     * @param id
     */
    @CacheEvict(value = "sysUser",key = "#id")
    public void deleteUser (int id){
        userMapper.deleteById(id);
    };

}
