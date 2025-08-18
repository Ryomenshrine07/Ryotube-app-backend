package com.ryotube.application.Helpers;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationData {
    private String username;
    private String email;
    private String password;
}
