package com.sky.aspect;
// 自定义切面，用于实现功能字段的自动填充逻辑
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import com.sky.annotation.AutoFill;
import com.sky.enumeration.OperationType;
import java.time.LocalDateTime;
import com.sky.context.BaseContext;
import java.lang.reflect.Method;
import com.sky.constant.AutoFillConstant;
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /*
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){
        log.info("切入点方法执行");
    }
    /*
     * 前置通知 在通知中进行公共字段的填充
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("前置通知执行");
        // 获取当前被拦截的放上的数据库操作类型
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class); //获得方法上的注解对象
        OperationType operationType = autoFill.value();//获取数据库操作类型

        // 获取当前被拦截方法的参数--实体对象
        Object[] args = joinPoint.getArgs();//获取方法的参数
        if(args==null||args.length==0){
            return;
        }
        Object entity = args[0];//获取方法的第一个参数
        if(entity==null){
            return;
        } 
        

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据当前不同的操作类型，为对应的属性通过反射来赋值
        if(operationType.equals(OperationType.INSERT)){
            // 为4个公共字段赋值
            try {
                // 公共字段赋值
               Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
               Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
               Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
               Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
               //通过反射为对象属性赋值
               setCreateTime.invoke(entity, now);
               setCreateUser.invoke(entity, currentId);
               setUpdateTime.invoke(entity, now);
               setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }else if(operationType.equals(OperationType.UPDATE)){
            // 为2个公共字段赋值
            try {
                // 公共字段赋值
               Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
               Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
               //通过反射为对象属性赋值
               setUpdateTime.invoke(entity, now);
               setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}