package com.podlasenko.example.controller;

import com.podlasenko.example.model.AuthenticationRequest;
import com.podlasenko.example.model.AuthenticationResponse;
import com.podlasenko.example.model.UserDTO;
import com.podlasenko.example.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/authenticate")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest request) throws Exception {
        String jwt = authenticationService.authenticate(request);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) {
        authenticationService.registerUser(user);
        return ResponseEntity.ok().build();
    }

}
