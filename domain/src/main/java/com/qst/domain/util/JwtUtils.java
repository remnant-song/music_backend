//package com.qst.domain.util;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.util.StringUtils;
//
//import java.util.Date;
//
//public class JwtUtils {
//
//    public static final long EXPIRE = 1000 * 60 * 60 * 24*7;//过期时间（毫秒为单位，此处为7天）
//    public static final String APP_SECRET = "Wc5yB5Uy1VGJqU+z9PDDPpjAo4z2YXLuEj8AiTnrMv0=";//秘钥（自己随便写）
//
//
//
//    public static String getJwtToken(Integer id,Integer role, String username){
//
//        String JwtToken = Jwts.builder()
//                .setHeaderParam("typ", "JWT")//jwet头信息
//                .setHeaderParam("alg", "HS256")
//                .setSubject("user_star")//分类（随便写）
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
//                .claim("id", id)//token载体（用户id和用户名）
//                .claim("username", username)
//                .claim("role",role)
//                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
//                .compact();
//        return JwtToken;
//    }
//
//    /**
//     * 判断token是否存在与有效
//     * @param jwtToken
//     * @return
//     */
//    public static boolean checkToken(String jwtToken) {
//        if(!StringUtils.hasLength(jwtToken)) return false;
//        try {
//            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
//        } catch (Exception e) {
//            //e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    public static Integer getMemberIdByJwtToken(String  jwtToken) {
//        if(!StringUtils.hasLength(jwtToken)) return null;
//        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
//        Claims claims = claimsJws.getBody();
//        return (Integer) claims.get("id");
//    }
//    public static String getMemberNameByJwtToken(String  jwtToken) {
//        if(!StringUtils.hasLength(jwtToken)) return null;
//        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
//        Claims claims = claimsJws.getBody();
//        return (String) claims.get("username");
//    }
//    public static Integer getMemberRoleByJwtToken(String  jwtToken) {
//        if(!StringUtils.hasLength(jwtToken)) return null;
//        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
//        Claims claims = claimsJws.getBody();
//        return (Integer) claims.get("role");
//    }
//}
//SecretKey 生成用 Keys.hmacShaKeyFor(...)。
//
//parser() 已废弃，jjwt 0.11.5 使用 parserBuilder().setSigningKey(...).build()。
//
//解析 Claims 抽取到 getClaims(...) 方法里，方便维护。
//
//使用 claims.get(..., 类型.class) 方法，避免类型转换错误。
//
//捕获 JwtException，涵盖 token 过期、签名无效、格式错误等。
//
//这样就可以在所有 controller 里安全调用 JwtUtils 的方法，而不会再因为 DatatypeConverter 在 JDK 9+ 缺失而崩溃。

package com.qst.domain.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

public class JwtUtils {

    public static final long EXPIRE = 1000L * 60 * 60 * 24 * 7; // 7天
    // 用于生成 key 的 secret，需要足够长，否则 jjwt 会抛异常
    public static final String APP_SECRET = "Wc5yB5Uy1VGJqU+z9PDDPpjAo4z2YXLuEj8AiTnrMv0=";

    // 将 APP_SECRET 转为 SecretKey
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(APP_SECRET));

    public static String getJwtToken(Integer id, Integer role, String username) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject("user_star")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .claim("id", id)
                .claim("username", username)
                .claim("role", role)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 判断token是否存在与有效
     */
    public static boolean checkToken(String jwtToken) {
        if (!StringUtils.hasLength(jwtToken)) return false;
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(jwtToken);
            return true;
        } catch (JwtException e) {
            // token 无效、被篡改、过期等
            return false;
        }
    }

    public static Integer getMemberIdByJwtToken(String jwtToken) {
        Claims claims = getClaims(jwtToken);
        return claims != null ? claims.get("id", Integer.class) : null;
    }

    public static String getMemberNameByJwtToken(String jwtToken) {
        Claims claims = getClaims(jwtToken);
        return claims != null ? claims.get("username", String.class) : null;
    }

    public static Integer getMemberRoleByJwtToken(String jwtToken) {
        Claims claims = getClaims(jwtToken);
        return claims != null ? claims.get("role", Integer.class) : null;
    }

    private static Claims getClaims(String jwtToken) {
        if (!StringUtils.hasLength(jwtToken)) return null;
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
        } catch (JwtException e) {
            // token 解析异常时返回 null
            return null;
        }
    }
}
