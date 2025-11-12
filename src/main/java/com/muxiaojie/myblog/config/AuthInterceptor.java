package com.muxiaojie.myblog.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.muxiaojie.myblog.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    // 排除的路径数组
    private static final String[] EXCLUDE_PATHS = {
        "/user/login",
        "/user/register", 
        "/user/doLogin",
        "/user/doRegister",
        "/css/**",
        "/js/**",
        "/images/**",
        "/error"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        String requestURI = request.getRequestURI();
        
        // 检查是否在排除路径中
        if (isExcludePath(requestURI)) {
            return true;
        }
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            // 未登录，重定向到登录页面
            response.sendRedirect("/user/login");
            return false;
        }
        
        return true;
    }

    /**
     * 检查请求路径是否在排除列表中
     */
    private boolean isExcludePath(String requestURI) {
        for (String path : EXCLUDE_PATHS) {
            if (path.endsWith("/**")) {
                // 处理通配符路径
                String basePath = path.substring(0, path.length() - 3);
                if (requestURI.startsWith(basePath)) {
                    return true;
                }
            } else if (requestURI.equals(path)) {
                // 精确匹配
                return true;
            }
        }
        return false;
    }
}