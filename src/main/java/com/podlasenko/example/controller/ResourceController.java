package com.podlasenko.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @GetMapping(path = "/users/hello")
    public String helloUser(){
        return "Hello User";
    }

    @GetMapping(path = "/admins/hello")
    public String helloAdmin(){
        return "Hello Admin";
    }
}
