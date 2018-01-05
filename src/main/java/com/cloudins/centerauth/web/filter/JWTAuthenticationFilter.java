package com.cloudins.centerauth.web.filter;


import com.cloudins.centerauth.entity.GrantedAuthorityImpl;
import com.cloudins.centerauth.entity.Role;
import com.cloudins.centerauth.entity.User;
import com.cloudins.centerauth.exception.JwtSignatureException;
import com.cloudins.centerauth.exception.JwtTokenExpiredException;
import com.cloudins.centerauth.service.SecurityUser;
import com.cloudins.centerauth.util.RedisUtil;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jdk.nashorn.internal.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * token的校验
 * 该类继承自BasicAuthenticationFilter，在doFilterInternal方法中，
 * 从http头的Authorization 项读取token数据，然后用Jwts包提供的方法校验token的合法性。
 * 如果校验通过，就认为这是一个取得授权的合法请求
 */
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private static Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
    private RedisTemplate<String,String> redisTemplate;
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,RedisTemplate redisTemplate) {
        super(authenticationManager);
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);

    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String userName = request.getHeader("user");
        if (token != null) {
            ArrayList<GrantedAuthority> authoritie = null;
            String user = null ;
          try{
              String tokenValue = token.replace("Bearer ", "");
              String  password = redisTemplate.opsForValue().get(userName+"password");
              user = Jwts.parser()
                      .setSigningKey(password)
//                    .parseClaimsJws(token.replace("Bearer ", ""))
                      .parseClaimsJws(tokenValue)
                      .getBody()
                      .getSubject();
              //从tocken中获取用户的权限
              Claims claims = Jwts.parser().setSigningKey(password).parseClaimsJws(tokenValue).getBody();
              ArrayList<GrantedAuthority>  arrayList = (ArrayList <GrantedAuthority>) claims.get("role");
              Object[] arrays = arrayList.toArray();
              authoritie = new ArrayList <>();
              for(int i=0;i<arrays.length;i++){
                  LinkedHashMap<String,String> map  = (LinkedHashMap <String, String>) arrays[i];
                  String role = map.get("authority");
                  authoritie.add(new GrantedAuthorityImpl(role));
              }
          }catch (Exception e){
              logger.info("解析Tocken出错！！！"+e.getMessage());
//              e.printStackTrace();
              return null;
          }
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null,authoritie);
            }
            return null;
        }
        return null;
    }

}
