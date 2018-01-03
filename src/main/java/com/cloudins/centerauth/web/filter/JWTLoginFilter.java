package com.cloudins.centerauth.web.filter;


import com.cloudins.centerauth.entity.User;
import com.cloudins.centerauth.service.impl.UserDetailsServiceImpl;
import com.cloudins.centerauth.util.CodeGeneratorUtil;
import com.cloudins.centerauth.util.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 验证用户名密码正确后，生成一个token，并将token返回给客户端
 * 该类继承自UsernamePasswordAuthenticationFilter，重写了其中的2个方法
 * attemptAuthentication ：接收并解析用户凭证。
 * successfulAuthentication ：用户成功登录后，这个方法会被调用，我们在这个方法里生成token。
 */
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // 接收并解析用户凭证
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            User user = new ObjectMapper()
                    .readValue(req.getInputStream(), User.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getName(),
                            user.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 用户成功登录后，这个方法会被调用，我们在这个方法里生成token
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        String password = CodeGeneratorUtil.generatorValue();
        String token = Jwts.builder()
                .setSubject(auth.getName())
                .setExpiration(new Date(System.currentTimeMillis() + 60*60 * 1000))
                .signWith(SignatureAlgorithm.HS512, password) //采用什么算法是可以自己选择的，不一定非要采用HS512
                .compact();
        String m = auth.getName()+"password";
        RedisUtil.getJedis().set(auth.getName(),token);
        RedisUtil.getJedis().set(auth.getName()+"password",password);
        res.addHeader("Authorization", "Bearer " + token);
    }
//    private void generateToken(HttpServletRequest req,
//                               HttpServletResponse res,
//                               FilterChain chain,
//                               Authentication auth){
//        Jwts.builder()
//                .setSubject(auth.getName())
//                .claim("")
//    }

}
