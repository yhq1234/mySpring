package com.example.service;

import com.example.entity.SysUser;
import com.example.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class SolrServiceImpl implements SolrService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Override
    public List<SysUser> addUser() {
        List<SysUser> list = new ArrayList<>();
        list = sysUserMapper.listUser();
        return list;
    }
}
