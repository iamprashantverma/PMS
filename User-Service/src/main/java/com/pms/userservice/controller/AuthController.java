package com.pms.userservice.controller;

import com.pms.userservice.dto.*;
import com.pms.userservice.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.dto.ForgetPasswordDTO;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signUp( @Valid @RequestBody UserDTO userDTO) {
        ResponseDTO resp = authService.signup(userDTO);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) throws InvalidCredentialsException {

        LoginResponseDTO loginResponse = authService.login(loginRequestDTO);

        // Create a cookie for the refresh token
        Cookie refreshTokenCookie = new Cookie("refresh_token", loginResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        // Add the cookie to the response
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(HttpServletRequest req,HttpServletResponse response)  {

        String refreshToken = Arrays.stream(req.getCookies())
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        ResponseDTO responseDTO =  authService.logout(refreshToken);
        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        // Add the cookie to the response
        response.addCookie(refreshTokenCookie);

        return  ResponseEntity.ok(responseDTO);
    }

    /* send the otp to their email address */
    @PostMapping("/otp")
    public ResponseEntity<ResponseDTO> sendOtp( @Valid @RequestBody ForgetPasswordDTO forgetPasswordDTO) {
        ResponseDTO res = authService.sendOtp(forgetPasswordDTO);
        return ResponseEntity.ok(res);
    }

    /* verify the token and reset their password */
    @PostMapping("/verify")
    public ResponseEntity<ResponseDTO> verifyOtp( @Valid @RequestBody VerifyResetPasswordDTO verifyResetPasswordDTO) {
        ResponseDTO res = authService.verifyOtp(verifyResetPasswordDTO);
        return ResponseEntity.ok(res);
    }

}
