package com.qst.login.util;

import com.qst.domain.entity.User;
import com.qst.domain.util.JwtUtils;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendMess {
    @Value("${service.ipAddr}")
    String ipAddr;

    public  Boolean SendActivation(User user, String Email){
        String token = JwtUtils.getJwtToken(0, user.getRole(), user.getUsername());
        try{
            HtmlEmail email=new HtmlEmail();
            email.setCharset("utf-8");
            email.addTo(Email);
            email.setHostName("smtp.qq.com");
            email.setSmtpPort(465);
            email.setSSLOnConnect(true);
            email.setFrom("3143893011@qq.com");
            email.setAuthentication("3143893011@qq.com","jrolyhbarwhgdfag");
            email.setSubject("账号激活");
            email.setMsg("请点击以下链接激活您的账号：  ".concat(ipAddr).concat("/login/activation?token=").concat(token));
            email.send();
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
}
