package com.ryotube.application.Services;

import com.ryotube.application.Entities.User;
import com.ryotube.application.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);
        if(user != null){
            return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                    new ArrayList<>()
            );
        }else{
            throw new UsernameNotFoundException(email);
        }
    }
}
