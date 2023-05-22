package com.example.demo.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.entity.User;
import com.example.demo.service.IUserService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenUtils {
    private static IUserService staticUserService;
    @Resource
    private IUserService userService;

    @PostConstruct
    public void setUserService(){
        staticUserService = userService;
    }

    /**
     * 生成token
     * @return
     */
    public static String genToken(String userId, String  sign)
    {
        return JWT.create().withAudience(userId)  // 将 user id保存到token里面，作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2))  // 2小时候token过期
                .sign(Algorithm.HMAC256(sign));  // 以password作为token密钥

    }

    /**
     * 获取当前登录的用户信息
     * @return
     */
    // 静态方法不可以引用非静态属性，因此前面要定义一个静态属性
    // 静态方法可以直接通过类名点方法名调用，不用new一个对象
    public static User getCurrentUser(){
        try
        {
            HttpServletRequest request =  ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (StrUtil.isNotBlank(token))
            {
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserService.getById(Integer.valueOf(userId));
            }
        }catch(Exception e)
        {
            return null;
        }
        return null;
    }
}
