package com.pms.userservice.services.impl;

import com.pms.userservice.entities.User;
import com.pms.userservice.exceptions.ResourceNotFound;
import com.pms.userservice.repositories.UserRepository;
import com.pms.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl   implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    /* get user by userId*/
    public User getUserById(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if ( userOptional.isEmpty() )
            throw new ResourceNotFound("email not registered , signup !");
        return userOptional.get();
    }

    /* load user by their email id */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);
        if ( userOptional.isEmpty() )
            throw new ResourceNotFound("email not registered , signup !");
        return userOptional.get();
    }

}
