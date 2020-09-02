package com.example.controller;

import com.example.entity.SysUser;
import com.example.service.SysUserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private SysUserService sysUserService;
    @RequestMapping("/")
    public String showHome(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("当前登录用户："+name);

        return "home";
    }
    @ApiOperation(value="用户登录", notes="")
    @RequestMapping("/login")
    public String showLogin(String username,  String password, Map<String,Object> map,
                            HttpSession session) {
        SysUser user = sysUserService.selectByName(username);

        if (user != null){

            session.setAttribute("user",user);
           // map.put("age",30);
            return "redirect:home";
        }else {
            session.invalidate();
            map.put("msg","用户名密码错误");
            return "login";
        }

    }
    @RequestMapping("/home")
    public String goMain(Map<String,Object> map)
    {
        map.put("name","zhangfang");
        map.put("age",28);
        map.put("sex","女");
        return "home";
    }

    @RequestMapping("/admin")
    @ResponseBody
    @ApiOperation(value = "返回首页",notes ="根据url的id来获取用户详细信息" )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String printAdmin(){
        return "如果你看见这句话，说明你有ROLE_ADMIN角色";
    }
    @RequestMapping("/user")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String printUser() {
        return "如果你看见这句话，说明你有ROLE_USER角色";
    }
}
