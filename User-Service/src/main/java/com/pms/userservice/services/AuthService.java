package com.pms.userservice.services;

import com.pms.userservice.dto.LoginRequestDTO;
import com.pms.userservice.dto.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    void logout(String refreshToken) ;

}
