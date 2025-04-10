package com.pms.userservice.services;

import com.pms.userservice.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;

/**
 * Service interface for handling authentication-related operations
 * such as login, signup, logout, OTP verification, and password reset.
 */
public interface AuthService {

    /**
     * Authenticates a user using provided credentials and creates a new session.
     *
     * @param loginRequestDTO the login request containing email and password
     * @return {@link LoginResponseDTO} containing access and refresh tokens
     * @throws InvalidCredentialsException if the credentials are incorrect
     */
    @Transactional
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws InvalidCredentialsException;

    /**
     * Registers a new user in the system.
     *
     * @param user the user details to be registered
     * @return {@link ResponseDTO} containing success or failure message
     */
    @Transactional
    ResponseDTO signup(UserDTO user);

    /**
     * Logs out the user by invalidating their session.
     *
     * @param refreshToken the refresh token associated with the user's session
     * @return {@link ResponseDTO} with logout confirmation
     */
    @Transactional
    ResponseDTO logout(String refreshToken);

    /**
     * Verifies the OTP sent to the user's email and allows password reset.
     *
     * @param verifyResetPasswordDTO the DTO containing email, OTP, new password, and windowId
     * @return {@link ResponseDTO} with result of verification and password reset status
     */
    @Transactional
    ResponseDTO verifyOtp(VerifyResetPasswordDTO verifyResetPasswordDTO);

    /**
     * Sends an OTP to the user's email for password reset request.
     *
     * @param forgetPasswordDTO the DTO containing email and browser windowId
     * @param httpServletRequest the HTTP servlet request (used to extract User-Agent)
     * @return {@link ResponseDTO} confirming the OTP has been sent
     */
    @Transactional
    ResponseDTO sendOtp(ForgetPasswordDTO forgetPasswordDTO, HttpServletRequest httpServletRequest);
}
