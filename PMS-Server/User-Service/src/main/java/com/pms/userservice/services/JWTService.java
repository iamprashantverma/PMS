package com.pms.userservice.services;

import com.pms.userservice.entities.User;

/**
 * Service interface for generating JWT (JSON Web Token) tokens.
 */
public interface JWTService {

    /**
     * Generates an access token for the given user.
     * Typically short-lived and used for authenticating API requests.
     *
     * @param user the user for whom the access token is generated
     * @return a JWT access token as a String
     */
    String generateAccessToken(User user);

    /**
     * Generates a refresh token for the given user.
     * Typically long-lived and used to obtain a new access token.
     *
     * @param user the user for whom the refresh token is generated
     * @return a JWT refresh token as a String
     */
    String generateRefreshToken(User user);
}
