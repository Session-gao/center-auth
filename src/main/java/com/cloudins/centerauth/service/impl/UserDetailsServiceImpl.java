package com.cloudins.centerauth.service.impl;


import com.cloudins.centerauth.entity.User;
import com.cloudins.centerauth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

//import boss.portal.repository.UserRepository;

/**
 * @author
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository myUserRepository;

    // 通过构造器注入MyUserRepository
    public UserDetailsServiceImpl(UserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User myUser = myUserRepository.findByName(username);
        if(myUser == null){
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(myUser.getName(), myUser.getPassword(), emptyList());
    }

}
