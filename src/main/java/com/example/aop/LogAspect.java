package com.example.aop;

import com.example.entity.SysLog;
import com.example.entity.SysUser;
import com.example.mapper.SysLogDao;
import com.example.utils.HttpContextUtils;
import com.example.utils.IpUtils;
import com.example.utils.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class LogAspect {
    @Autowired
    private SysLogDao sysLogDao;
    @Pointcut("@annotation(com.example.utils.Log)")
    public void pointcut(){}
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point){
        Object result = null;
        long beginTime = System.currentTimeMillis();
        try {
            result = point.proceed();
        }catch (Throwable e){
            e.printStackTrace();
        }
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        // 保存日志
        saveLog(point, time);
        return result;
    }
    private void saveLog(ProceedingJoinPoint joinPoint, long time){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = new SysLog();
        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation != null){
            //注解上的描述
            sysLog.setOperation(logAnnotation.value());
        }
        String className = joinPoint.getTarget().getClass().getName();
        String methodName =signature.getName();
        sysLog.setMethod(className+"."+methodName+"()");
        // 请求的方法参数值
        Object[] args = joinPoint.getArgs();
        //请求的方法参数名称
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if (args != null && paramNames != null) {
            String params = "";
            for (int i = 0; i < args.length; i++) {
                params += "  " + paramNames[i] + ": " + args[i];
            }
            sysLog.setParams(params);
        }
        // 获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        // 设置IP地址
        sysLog.setIp(IpUtils.getIpAddr(request));
        // 模拟一个用户名
         SysUser user = (SysUser) request.getSession().getAttribute("user");
        String username = user.getName();
        sysLog.setUsername(username);
        sysLog.setTime((int) time);
        sysLog.setCreateTime(new Date());
        // 保存系统日志
        sysLogDao.saveSysLog(sysLog);

    }

}
