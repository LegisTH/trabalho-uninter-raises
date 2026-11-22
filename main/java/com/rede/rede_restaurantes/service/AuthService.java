
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.dto.LoginRequest;
import com.rede.rede_restaurantes.dto.LoginResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.senha()
                        )
                );

        UserDetails usuario = (UserDetails) authentication.getPrincipal();

        String token = jwtService.gerarToken(usuario);

        return new LoginResponse(token, "Bearer");
    }
}
