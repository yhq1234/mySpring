package com.example.controller;


import com.example.entity.SysUser;
import com.example.service.SysUserService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author:0xOO
 * @Date: 2018/9/26 0026
 * @Time: 14:42
 */

@RestController
@RequestMapping("/testBoot")
public class UserController {

    @Autowired
    private SysUserService userService;

    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public SysUser getUser(Integer id){
        SysUser sysUser = userService.selectById(id);
        return sysUser;
    }
}
