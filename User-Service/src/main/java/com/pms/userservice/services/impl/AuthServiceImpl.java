package com.pms.userservice.services.impl;

import com.pms.userservice.dto.LoginRequestDTO;
import com.pms.userservice.dto.LoginResponseDTO;
import com.pms.userservice.entities.User;
import com.pms.userservice.services.AuthService;
import com.pms.userservice.services.JWTService;
import com.pms.userservice.services.SessionService;
import com.pms.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final UserService   userService;
    private final SessionService sessionService;


    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String userEmail = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();

        /* get the user  to verify credentials */
        User user = (User) userService.loadUserByUsername(userEmail);

        if ( !passwordEncoder.matches(password,user.getPassword()))
            throw  new BadCredentialsException("invalid password ");

        /* generate the access token and refresh token */
        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        /* create the new session */
        sessionService.generateNewSession(user,refreshToken);
        return LoginResponseDTO.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();

    }

    @Override
    public void logout(String refreshToken) {
        sessionService.deleteSession(refreshToken);
    }


}
