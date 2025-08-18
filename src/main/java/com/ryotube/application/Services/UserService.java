package com.ryotube.application.Services;


import com.ryotube.application.Entities.User;
import com.ryotube.application.Helpers.AuthenticationData;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.UserRepository;
import com.ryotube.application.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    ChannelService channelService;



    @Transactional
    public User registerUser(AuthenticationData authData) throws Exception{
        User user = userRepository.getUserByEmail(authData.getEmail());
        System.out.println("Got Here");
        if(user == null){
            User u = new User();
            u.setUsername(authData.getUsername());
            u.setEmail(authData.getEmail());
            u.setPassword(passwordEncoder.encode(authData.getPassword()));
            User savedUser = userRepository.save(u);
            channelService.createChannel(savedUser);
            return savedUser;
        }else{
            throw new Exception("Email already exists");
        }
    }
    public User getUserDetails(String email) throws Exception{
        User user = userRepository.getUserByEmail(email);
        if(user == null){
            throw new Exception("User not found with given email");
        }else{
            return user;
        }
    }
}
