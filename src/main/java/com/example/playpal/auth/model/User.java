package com.example.playpal.auth.model;

import com.example.playpal.common.security.UserType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private UserType userType;
    private Date lastLogout;
}
