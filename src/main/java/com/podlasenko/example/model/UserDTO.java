package com.podlasenko.example.model;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String role;
}
