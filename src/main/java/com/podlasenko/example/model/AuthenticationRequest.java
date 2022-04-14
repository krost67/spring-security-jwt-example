package com.podlasenko.example.model;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private final String username;
    private final String password;
}
