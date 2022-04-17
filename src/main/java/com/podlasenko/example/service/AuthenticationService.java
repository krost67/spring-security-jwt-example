package com.podlasenko.example.service;

import com.podlasenko.example.entity.UserEntity;
import com.podlasenko.example.model.AuthenticationRequest;
import com.podlasenko.example.model.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JWTHelperService jwtHelperService;

    public String authenticate(AuthenticationRequest request) throws Exception {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword());

            authenticationManager.authenticate(authenticationToken);
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());
        return jwtHelperService.generateToken(userDetails);
    }

    public UserEntity registerUser(UserDTO user) {
        return customUserDetailsService.save(user);
    }
}
