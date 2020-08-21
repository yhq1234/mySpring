package com.example.controller;


import com.example.utils.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/web")
public class TestController {
    @Log("执行方法一")
    @RequestMapping("/one")
    @ResponseBody
    public Map methodOne(String name){
        System.out.println("==========================");
        Map<String,Object> map = new HashMap<>();
        map.put("yyy","123");
        return map;
    }

    @Log("执行方法二")
    @RequestMapping("/two")
    public String methodTwo() throws InterruptedException {

        Thread.sleep(2000);
        return "/home";

    }

    @Log("执行方法三")
    @GetMapping("/three")
    public void methodThree(String name,String age){}
}
