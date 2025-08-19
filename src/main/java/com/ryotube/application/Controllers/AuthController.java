package com.ryotube.application.Controllers;

import com.ryotube.application.Entities.User;
import com.ryotube.application.Helpers.AuthenticationData;
import com.ryotube.application.Helpers.TokenData;
import com.ryotube.application.Repositories.UserRepository;
import com.ryotube.application.Services.MyUserDetailService;
import com.ryotube.application.Services.UserService;
import com.ryotube.application.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    MyUserDetailService myUserDetailService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody AuthenticationData authRequest) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect Username Or Password");
        }
        final UserDetails userDetails = myUserDetailService.loadUserByUsername(authRequest.getEmail());
        final String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new TokenData(token));
    }
    @PostMapping("/register")
    ResponseEntity<?> registerUser(@RequestBody AuthenticationData authRequest){
        try {
            return ResponseEntity.ok(userService.registerUser(authRequest));
        } catch (Exception e) {
            if(e.getMessage().equals("Email already exists")){
                return ResponseEntity.ok("Email already exists");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/get-user")
    public ResponseEntity<UserDetails> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // 1. Your Axios interceptor adds the 'Authorization' header.
        // 2. Your JwtFilter automatically reads the header and validates the token.
        // 3. The filter loads the user and places it in the security context.
        // 4. @AuthenticationPrincipal injects that user directly into this method.

        return ResponseEntity.ok(userDetails);
    }
    @PostMapping("/get-user-by-email")
    public ResponseEntity<User> getUserByEmail(@RequestBody AuthenticationData authenticationData){
        User u = userRepository.getUserByEmail(authenticationData.getEmail());
        return ResponseEntity.ok(u);
    }
}
