package com.pms.userservice.services;

import com.pms.userservice.entities.User;

/**
 * Service interface for managing user sessions using refresh tokens.
 */
public interface SessionService {

    /**
     * Creates and stores a new session when a user logs in.
     *
     * @param user the authenticated user
     * @param refreshToken the refresh token associated with the session
     */
    void generateNewSession(User user, String refreshToken);

    /**
     * Validates the session using the provided refresh token.
     * Typically used to check if a token is still active and valid.
     *
     * @param refreshToken the refresh token to validate
     * @return true if the session is valid, false otherwise
     */
    boolean validateSession(String refreshToken);

    /**
     * Deletes the session associated with the given refresh token.
     * Used during user logout or session expiration.
     *
     * @param refreshToken the refresh token whose session is to be deleted
     */
    void deleteSession(String refreshToken);
}
