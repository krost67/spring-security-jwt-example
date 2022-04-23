package com.podlasenko.example.filter;

import com.podlasenko.example.service.JWTHelperService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomJwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTHelperService jwtHelperService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String jwtToken = extractJwtFromRequest(request);

        try {
            if (StringUtils.hasText(jwtToken) && jwtHelperService.validateToken(jwtToken)) {
                UserDetails userDetails = new User(
                        jwtHelperService.getUsernameFromToken(jwtToken),
                        "",
                        jwtHelperService.getRolesFromToken(jwtToken));

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                log.debug("Cannot set the Security Context");
            }
        } catch (ExpiredJwtException ex) {
            processExpiredToken(request, ex);
        } catch (BadCredentialsException ex) {
            request.setAttribute("exception", ex);
            throw ex;
        }

        chain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private void processExpiredToken(HttpServletRequest request, ExpiredJwtException ex) {
        String isRefreshToken = request.getHeader("isRefreshToken");
        String requestURL = request.getRequestURL().toString();

        if (isRefreshToken != null &&
                isRefreshToken.equals("true") &&
                requestURL.contains("refresh")) {
            allowRefreshToken(ex, request);
        } else {
            request.setAttribute("exception", ex);
            throw ex;
        }

    }

    private void allowRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {
        // create a UsernamePasswordAuthenticationToken with null values.
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                null, null, null);
        // After setting the Authentication in the context, we specify
        // that the current user is authenticated. So it passes the
        // Spring Security Configurations successfully.
        SecurityContextHolder.getContext()
                .setAuthentication(usernamePasswordAuthenticationToken);
        // Set the claims so that in controller we will be using it to create a new JWT
        request.setAttribute("claims", ex.getClaims());
    }

}
