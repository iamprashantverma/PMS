package com.pms.userservice.services;

import com.pms.userservice.entities.User;


public interface JWTService {

    public String generateAccessToken(User user);
    public String generateRefreshToken(User user);
    //    public String getUserIdFromToken(String token);
}
