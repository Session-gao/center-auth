package com.cloudins.centerauth.controller;


import com.cloudins.centerauth.entity.User;
import com.cloudins.centerauth.exception.JwtIllegalArgumentException;
import com.cloudins.centerauth.exception.JwtSignatureException;
import com.cloudins.centerauth.exception.JwtTokenExpiredException;
import com.cloudins.centerauth.exception.UsernameIsExitedException;
import com.cloudins.centerauth.repository.UserRepository;
import com.cloudins.centerauth.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository applicationUserRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /*public UserController(UserRepository myUserRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.applicationUserRepository = myUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }*/

    @RequestMapping("/userList")
    @ResponseBody
    public Map<String, Object> userList(){
        List<User> myUsers = applicationUserRepository.findAll();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("users",myUsers);
        return map;
    }
    @RequestMapping("/edit/userList")
    @ResponseBody
    public Map<String, Object> userListEdit(){
        List<User> myUsers = applicationUserRepository.findAll();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("users",myUsers);
        return map;
    }
    @RequestMapping("/new/userList")
    @ResponseBody
    public Map<String, Object> userListView(){
        List<User> myUsers = applicationUserRepository.findAll();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("users",myUsers);
        return map;
    }

    /**
     * 该方法是注册用户的方法，默认放开访问控制
     * @param user
     */
    @PostMapping("/signup")
    public void signUp(HttpServletRequest request) {
       String username =  request.getHeader("username");
       String password = request.getHeader("password");
       User user = new User();
       user.setName(username);
       user.setPassword(password);

        User user1 = applicationUserRepository.findByName(username);
        if(null != user1){
            throw new UsernameIsExitedException("用户已经存在~");
        }
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword()).getBytes()));
        applicationUserRepository.save(user);
    }
    @RequestMapping("/id")
    public String getuser(){
        return "ssss";
    }

    /**
     * 退出系统方法
     * @param request
     * @return
     */
    @RequestMapping(value = "/quit",method = RequestMethod.POST)
    public String   quiteSystem(HttpServletRequest request){
        String user = request.getHeader("user");
        RedisUtil.getJedis().del(user);
        RedisUtil.getJedis().del(user+"password");
        return "sign out success!!";

    }
    @RequestMapping(value = "/authon",method = RequestMethod.GET)
    @ResponseBody
    public UsernamePasswordAuthenticationToken authon(HttpServletRequest request) throws JwtIllegalArgumentException, JwtSignatureException, JwtTokenExpiredException {
        String tt = request.getParameter("Authorization");
        String token = request.getHeader("Authorization");
        String userName = request.getHeader("user");
        if (token != null) {
            String tokenValue = token.replace("Bearer ", "");
            // parse the token.
//          String userName =  RedisUtil.getJedis().get(tokenValue);
            String password =   RedisUtil.getJedis().get(userName+"password");
            String user = null;
            try{
                user = Jwts.parser()
                        .setSigningKey(password)
//                    .parseClaimsJws(token.replace("Bearer ", ""))
                        .parseClaimsJws(tokenValue)
                        .getBody()
                        .getSubject();

            }catch (ExpiredJwtException ex){
                throw new JwtTokenExpiredException("User token expired!");
            }catch (SignatureException ex){
                throw new JwtSignatureException("User token signature error!");
            }catch (IllegalArgumentException ex){
                throw new JwtIllegalArgumentException("User token is null or empty!");
            }finally {
                if (user != null) {
                     return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                }
                return null;
            }
        }
        return null;
    }


}
