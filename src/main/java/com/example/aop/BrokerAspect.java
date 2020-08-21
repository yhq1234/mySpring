package com.example.aop;

import com.example.entity.SysUser;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Aspect
@Component
public class BrokerAspect {
/*
*
     * 定义切入点，切入点为com.example.demo.aop.AopController中的所有函数
     *通过@Pointcut注解声明频繁使用的切点表达式
*/


    @Pointcut("within(com.example.controller.*)&& !within(com.example.controller.LoginController))")
    public void BrokerAspect(){

    }
    @Before("BrokerAspect()")
    public void doBeforeGame(JoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        SysUser user = (SysUser) request.getSession().getAttribute("user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object details = authentication.getDetails();
        System.out.println("经纪人正在处理球星赛前事务！");
        if (user == null) {
            System.out.println("-----------用户未登录-----------");
            attributes.getResponse().sendRedirect("/login"); // 手动转发到/login映射路径
        }
        System.out.println("-----------用户已登录-----------");
        // 一定要指定Object返回值，若AOP拦截的Controller
        // return了一个视图地址，那么本来Controller应该跳转到这个视图地址的，但是被AOP拦截了，那么原来Controller仍会执行return，但是视图地址却找不到404了
        // 切记一定要调用proceed()方法
        // proceed()：执行被通知的方法，如不调用将会阻止被通知的方法的调用，也就导致Controller中的return会404
        //return joinPoint.proceed();
    }
/*
*
     * @description  在连接点执行之后执行的通知（返回通知和异常通知的异常）
*/


    @After("BrokerAspect()")
    public void doAfterGame(){
        System.out.println("经纪人为球星表现疯狂鼓掌！");
    }




}
