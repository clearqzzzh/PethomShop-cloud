package com.kk.pethome.basic.jwt;

import com.aliyuncs.utils.StringUtils;
import com.kk.pethome.user.pojo.loginInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;
@Slf4j
public class LoginContext {

    /**
     *
     * @param request
     * @return 如果是后台需要返回employee，如果是前台返回user
     */
    public static Object getLoginUser(HttpServletRequest request){
        try {
            String token = request.getHeader("token");
            if (StringUtils.isEmpty(token))
                return null;
            //通过公钥解析token，登陆载荷体
            PublicKey publicKey = RsaUtils.getPublicKey(JwtUtils.class.getClassLoader().getResource("hrm_auth_rsa.pub").getFile());
            Payload<PayloadDate> payload = JwtUtils.getInfoFromToken(token, publicKey, PayloadDate.class);
            PayloadDate payloadData = payload.getUserInfo();
            if (payloadData==null){
                log.error("payloadData is null!");
                return null;
            }
            loginInfo loginInfo = payloadData.getLogininfo();
            if (payloadData.getLogininfo()!=null){
                return loginInfo;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
return null;
    }
}