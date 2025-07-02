package com.qst.login.util;

import com.qst.domain.entity.User;
import com.qst.domain.util.JwtUtils;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
@Component
public class SendMess {
//    @Value("${service.ipAddr}")
//    String ipAddr;

    @Value("${server.port}")
    int serverPort;  // 从配置里取端口
    private String cachedIpAddr; // 缓存结果，避免每次都重新计算
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
            //原为请点击以下链接激活您的账号：  ".concat(ipAddr).
            email.setMsg("请点击以下链接激活您的账号：  ".concat(getServerAddress()).concat("/login/activation?token=").concat(token));
            email.send();
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    // 获取服务器地址（本机IP + 端口）
    private String getServerAddress() throws Exception {
        if (cachedIpAddr != null) return cachedIpAddr;  // 已经算过直接用

        String ip = null;
        // 遍历所有网卡，找到一个非回环、非虚拟机、非docker的有效IP
        Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
        while (nics.hasMoreElements()) {
            NetworkInterface nic = nics.nextElement();
            if (!nic.isUp() || nic.isLoopback() || nic.isVirtual()) continue;
            Enumeration<InetAddress> addrs = nic.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                    ip = addr.getHostAddress();
                    break;
                }
            }
            if (ip != null) break;
        }
        if (ip == null) ip = InetAddress.getLocalHost().getHostAddress(); // 退而求其次

        cachedIpAddr = "http://" + ip + ":" + serverPort;
        return cachedIpAddr;
    }
}
