package com.example.mapper;

import com.example.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Mapper
public interface SysLogDao {
    void saveSysLog(SysLog sysLog);
}
