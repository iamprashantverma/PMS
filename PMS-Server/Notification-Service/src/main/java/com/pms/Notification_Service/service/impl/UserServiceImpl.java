package com.pms.Notification_Service.service.impl;

import com.pms.Notification_Service.entities.UserDetails;
import com.pms.Notification_Service.repositories.UserRepository;
import com.pms.Notification_Service.service.UserService;
import com.pms.userservice.events.UserSignupEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private UserDetails convertUserSignUpEventToUserDetailsEntity(UserSignupEvent event) {
        return modelMapper.map(event, UserDetails.class);
    }

    @Override
    public void saveUserDetails(UserSignupEvent event) {
        log.info("Saving new user details for user ID: {}", event.getUserId());
        UserDetails userDetails = convertUserSignUpEventToUserDetailsEntity(event);
        userRepository.save(userDetails);
        log.info("User details saved successfully for user ID: {}", event.getUserId());
    }

    @Override
    public void updateUserDetails(UserSignupEvent event) {
        log.info("Updating user details for user ID: {}", event.getUserId());
        UserDetails existingUser = userRepository.findById(event.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found for ID: {}", event.getUserId());
                    return new IllegalArgumentException("User not found for ID: " + event.getUserId());
                });

        modelMapper.map(event, existingUser);
        UserDetails user =  userRepository.save(existingUser);
        log.info("User details updated successfully for:{}", user);
    }

    @Override
    public void removeUserDetails(UserSignupEvent event) {
        log.info("Removing user details for user ID: {}", event.getUserId());
        if (userRepository.existsById(event.getUserId())) {
            userRepository.deleteById(event.getUserId());
            log.info("User details removed successfully for user ID: {}", event.getUserId());
        } else {
            log.warn("Attempted to remove non-existing user details for user ID: {}", event.getUserId());
        }
    }
}
