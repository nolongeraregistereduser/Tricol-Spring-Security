package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.Request.LoginRequest;
import com.tricol.springboottricolapi.dto.Request.RegisterRequest;
import com.tricol.springboottricolapi.dto.Response.AuthResponse;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
    void register(RegisterRequest request);
    AuthResponse refreshToken(String refreshTokenStr);
    void logout(String username);
}
