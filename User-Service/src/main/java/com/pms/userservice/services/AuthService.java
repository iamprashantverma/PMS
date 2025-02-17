package com.pms.userservice.services;

import com.pms.userservice.dto.*;
import jakarta.transaction.Transactional;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;

public interface AuthService {

    /* login user and create new session */
    @Transactional
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws InvalidCredentialsException;

    /* create new user*/
    @Transactional
    ResponseDTO signup(UserDTO user);

    @Transactional
    /* handle logout and delete the session*/
    void logout(String refreshToken) ;

    /* verify sent  otp and reset the password */
    @Transactional
    ResponseDTO verifyOtp(VerifyResetPasswordDTO verifyResetPasswordDTO);

    /* send the password reset otp to user email */
    @Transactional
    ResponseDTO sendOtp(com.example.dto.ForgetPasswordDTO forgetPasswordDTO);
}
