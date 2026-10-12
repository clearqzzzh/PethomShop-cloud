package com.kk.pethome.basic.jwt;

import com.kk.pethome.basic.ann.PreAuthorize;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.PublicKey;

/**
 * JWT鉴权拦截器：验证token + 校验@PreAuthorize权限
 */
public class JwtInterceptor implements HandlerInterceptor {

    private PublicKey publicKey;

    public JwtInterceptor(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 放行非Controller方法（如静态资源）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 1. 从请求头获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"未登录，请先登录\"}");
            return false;
        }

        // 去掉Bearer前缀（如果有的话）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 2. 解析token
        Payload<PayloadDate> payload;
        try {
            payload = JwtUtils.getInfoFromToken(token, publicKey, PayloadDate.class);
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"token无效或已过期\"}");
            return false;
        }

        // 3. 检查token是否过期
        if (payload.getExpiration() != null && payload.getExpiration().before(new java.util.Date())) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"token已过期，请重新登录\"}");
            return false;
        }

        // 4. 将用户信息存入request属性，供Controller使用
        PayloadDate payloadDate = payload.getUserInfo();
        request.setAttribute("payloadDate", payloadDate);
        if (payloadDate != null && payloadDate.getLogininfo() != null) {
            request.setAttribute("currentUserId", payloadDate.getLogininfo().getId());
            request.setAttribute("currentUsername", payloadDate.getLogininfo().getUsername());
        }

        // 5. 检查@PreAuthorize权限注解
        // 先看方法级别
        PreAuthorize methodAnnotation = handlerMethod.getMethodAnnotation(PreAuthorize.class);
        // 再看类级别
        PreAuthorize classAnnotation = handlerMethod.getBeanType().getAnnotation(PreAuthorize.class);

        PreAuthorize annotation = methodAnnotation != null ? methodAnnotation : classAnnotation;
        if (annotation != null) {
            String requiredSn = annotation.value();
            if (payloadDate == null || payloadDate.getPermission() == null
                    || !payloadDate.getPermission().contains(requiredSn)) {
                response.setStatus(403);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"权限不足，需要权限: " + annotation.name() + "(" + requiredSn + ")\"}");
                return false;
            }
        }

        return true;
    }
}
