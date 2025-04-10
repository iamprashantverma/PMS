package com.pms.userservice.services.impl;

import com.pms.userservice.dto.*;
import com.pms.userservice.entities.PasswordReset;
import com.pms.userservice.entities.User;
import com.pms.userservice.entities.enums.Status;
import com.pms.userservice.events.PasswordResetRequestedEvent;
import com.pms.userservice.exceptions.InvalidRequestException;
import com.pms.userservice.exceptions.ResourceAlreadyExist;
import com.pms.userservice.exceptions.ResourceNotFound;
import com.pms.userservice.producer.UserEventProducer;
import com.pms.userservice.repositories.PasswordResetRepository;
import com.pms.userservice.repositories.UserRepository;
import com.pms.userservice.services.AuthService;
import com.pms.userservice.services.JWTService;
import com.pms.userservice.services.SessionService;
import com.pms.userservice.services.UserService;
import com.pms.userservice.utils.OtpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final UserService userService;
    private final SessionService sessionService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final UserEventProducer eventProducer;

    // Converts DTO to User entity
    private User convertToUserEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    // Converts User entity to DTO
    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }


    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws InvalidCredentialsException {
        String userEmail = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();

        // Get the user by email
        User user = userService.findUserByEmail(userEmail);

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Store session using refresh token
        sessionService.generateNewSession(user, refreshToken);

        return LoginResponseDTO.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }


    @Override
    public ResponseDTO signup(UserDTO user) {
        // Check if user already exists
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent()) {
            throw new ResourceAlreadyExist("Email is already registered, please login.");
        }

        // Map DTO to entity and setup defaults
        User toBeCreated = convertToUserEntity(user);
        toBeCreated.setStatus(Status.ACTIVE);
        toBeCreated.setJoinedAt(LocalDate.now());

        // Encode password
        String hashPass = passwordEncoder.encode(user.getPassword());
        toBeCreated.setPassword(hashPass);

        // Save user
        userRepository.save(toBeCreated);

        return ResponseDTO.builder()
                .message("Signup successful!")
                .build();
    }

    @Override
    public ResponseDTO logout(String refreshToken) {
        sessionService.deleteSession(refreshToken);
        return ResponseDTO.builder()
                .message("Logout successful")
                .build();
    }


    @Override
    public ResponseDTO sendOtp(ForgetPasswordDTO forgetPasswordDTO, HttpServletRequest request) {
        String email = forgetPasswordDTO.getEmail();
        String userAgent = request.getHeader("User-Agent");

        // Verify user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("User not found: " + email));

        // Check existing reset request
        PasswordReset existingReset = passwordResetRepository.findByEmail(email);
        if (existingReset != null && !existingReset.isExpired()) {
            throw new ResourceAlreadyExist("OTP already sent. Please try again later.");
        }

        // Clean up expired record if present
        if (existingReset != null) {
            passwordResetRepository.delete(existingReset);
        }

        // Generate OTP
        String otp = OtpUtil.generateCustomOtp();

        // Create and save reset entity
        PasswordReset toBeCreated = PasswordReset.builder()
                .otp(otp)
                .expiryAt(LocalDateTime.now().plusMinutes(5))
                .email(email)
                .userAgent(userAgent)
                .windowId(forgetPasswordDTO.getWindowId())
                .build();

        toBeCreated = passwordResetRepository.save(toBeCreated);

        // Send event to Kafka for notification
        PasswordResetRequestedEvent event = PasswordResetRequestedEvent.builder()
                .id(toBeCreated.getId())
                .name(user.getName())
                .userId(user.getUserId())
                .email(email)
                .requestedAt(toBeCreated.getCreateAt())
                .otp(otp)
                .build();

        eventProducer.sendPasswordResetRequestedEvent(event);

        return ResponseDTO.builder()
                .message("OTP sent to your email. It's valid for 5 minutes.")
                .build();
    }

    @Override
    public ResponseDTO verifyOtp(VerifyResetPasswordDTO verifyResetPasswordDTO) {
        String email = verifyResetPasswordDTO.getEmail();
        String windowId = verifyResetPasswordDTO.getWindowId();
        String otp = verifyResetPasswordDTO.getOtp();

        // Find reset request
        PasswordReset passwordReset = passwordResetRepository.findByEmail(email);
        if (passwordReset == null) {
            throw new ResourceNotFound("No OTP request found for this email.");
        }

        if (!passwordReset.getOtp().equals(otp)) {
            throw new InvalidRequestException("Invalid OTP.");
        }

        if (!passwordReset.getWindowId().equals(windowId)) {
            throw new InvalidRequestException("This OTP must be verified in the same browser window where it was requested.");
        }

        if (passwordReset.isExpired()) {
            throw new InvalidRequestException("OTP has expired.");
        }

        passwordResetRepository.delete(passwordReset);

        // Reset user password
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("Invalid email: " + email));

        String newPassword = verifyResetPasswordDTO.getNewPassword();
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);

        userRepository.save(user);

        return ResponseDTO.builder()
                .message("Password reset successfully. You can now login.")
                .build();
    }
}
