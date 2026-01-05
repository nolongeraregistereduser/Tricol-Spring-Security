package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.entity.RefreshToken;
import com.tricol.springboottricolapi.entity.UserApp;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken(UserApp user);
    RefreshToken verifyExpiration(RefreshToken token);
    RefreshToken findByToken(String token);
    void deleteByUser(UserApp user);
}
