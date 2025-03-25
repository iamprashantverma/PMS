package com.pms.userservice.services.impl;


import com.pms.userservice.dto.*;
import com.pms.userservice.entities.User;
import com.pms.userservice.entities.enums.Roles;
import com.pms.userservice.entities.enums.Status;
import com.pms.userservice.exceptions.ResourceAlreadyExist;
import com.pms.userservice.repositories.UserRepository;
import com.pms.userservice.services.AuthService;
import com.pms.userservice.services.JWTService;
import com.pms.userservice.services.SessionService;
import com.pms.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final UserService   userService;
    private final SessionService sessionService;
    private final ModelMapper modelMapper ;
    private final UserRepository userRepository;

    private User convertToUserEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
    private UserDTO convertToUserDTO (User user) {
        return modelMapper.map(user,UserDTO.class);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws InvalidCredentialsException {
        String userEmail = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();

        /* get the user  to verify credentials */
        User user = userService.findUserByEmail(userEmail);

        if ( !passwordEncoder.matches(password,user.getPassword()))
            throw  new InvalidCredentialsException("invalid password ");

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
    public ResponseDTO signup(UserDTO user) {

        /* checked if user is already registered  */
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isPresent())
            throw  new ResourceAlreadyExist("email is already registered , login !");
        /* convert into the user entity */
        User toBeCreated = convertToUserEntity(user);

        /* set up the initial fields */
        toBeCreated.setStatus(Status.ACTIVE);
        toBeCreated.setRole(Roles.USER);
        String password = user.getPassword();
        /* hash the password*/
        String hashPass = passwordEncoder.encode(password);

        /* set into user Entity */
        toBeCreated.setPassword(hashPass);

        /* save the new user to db */
        User savedUser  =  userRepository.save(toBeCreated);

        /* return the response */
        return ResponseDTO.builder().message("Signup successful!").build();
    }

    @Override
    public ResponseDTO logout(String refreshToken) {
        sessionService.deleteSession(refreshToken);
        return ResponseDTO.builder()
                .message("logout Successfully")
                .build();
    }

    @Override
    public ResponseDTO verifyOtp(VerifyResetPasswordDTO verifyResetPasswordDTO) {
        return null;
    }

    @Override
    public ResponseDTO sendOtp(ForgetPasswordDTO forgetPasswordDTO) {
        return null;
    }


}
