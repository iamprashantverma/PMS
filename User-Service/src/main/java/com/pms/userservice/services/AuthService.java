package com.pms.userservice.services;

import com.pms.userservice.dto.LoginRequestDTO;
import com.pms.userservice.dto.LoginResponseDTO;
import com.pms.userservice.dto.ResponseDTO;
import com.pms.userservice.dto.UserDTO;
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




}
