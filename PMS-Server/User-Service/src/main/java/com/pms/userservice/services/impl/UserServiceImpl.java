package com.pms.userservice.services.impl;

import com.pms.userservice.dto.ResponseDTO;
import com.pms.userservice.dto.UserDTO;
import com.pms.userservice.entities.User;
import com.pms.userservice.entities.enums.Roles;
import com.pms.userservice.entities.enums.Status;
import com.pms.userservice.events.UserSignupEvent;
import com.pms.userservice.events.enums.EventType;
import com.pms.userservice.exceptions.ResourceNotFound;
import com.pms.userservice.producer.UserEventProducer;
import com.pms.userservice.repositories.UserRepository;
import com.pms.userservice.services.CloudinaryService;
import com.pms.userservice.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;
    private final UserEventProducer userEventProducer;

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private User convertToUserEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    private UserSignupEvent createUserSignupEvent(User user) {
        return UserSignupEvent.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .commentMentions(user.getCommentMentions())
                .emailUpdates(user.getEmailUpdates())
                .bugUpdates(user.getBugUpdates())
                .name(user.getName())
                .subTaskUpdates(user.getSubTaskUpdates())
                .taskUpdates(user.getTaskUpdates())
                .build();
    }

    @Override
    public User getUserById(String userId) {
        log.debug("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("Invalid User ID!"));
    }

    @Override
    public User findUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("Email not registered, please sign up!"));
    }

    @Override
    @Transactional
    public ResponseDTO assignRoleToUser(String userId, Roles role) {
        log.info("Assigning role [{}] to user ID: {}", role, userId);
        User user = getUserById(userId);
//        user.setRole(role);
        userRepository.save(user);
        log.info("Role [{}] successfully assigned to user ID: {}", role, userId);
        return ResponseDTO.builder().message("Role assigned successfully").build();
    }

    @Override
    public UserDTO getUserDetails(String userId) {
        log.info("Retrieving details for user ID: {}", userId);
        return convertToUserDTO(getUserById(userId));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::convertToUserDTO)
                .toList();
    }

    @Override
    @Transactional
    public ResponseDTO updateUserDetails(UserDTO userDTO, MultipartFile file) {
        log.info("Updating user details for user ID: {}", userDTO.getUserId());

        User existingUser = getUserById(userDTO.getUserId());
        existingUser.setName(userDTO.getName());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setDob(userDTO.getDob());
        existingUser.setGender(userDTO.getGender());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhoneNo(userDTO.getPhoneNo());

        String profileUrl = cloudinaryService.uploadImage(file);
        existingUser.setImage(profileUrl);

        User modifiedUser = userRepository.save(existingUser);
        log.info("User details updated in database for user ID: {}", userDTO.getUserId());

        UserSignupEvent event = createUserSignupEvent(modifiedUser);
        event.setEventType(EventType.USER_UPDATED);
        userEventProducer.sendUserSignupEvent(event);
        log.info("User update event sent to Kafka for user ID: {}", userDTO.getUserId());

        return ResponseDTO.builder().message("User details updated successfully").build();
    }

    @Override
    @Transactional
    public ResponseDTO deactivateUser(String userId) {
        log.info("Deactivating user with ID: {}", userId);
        User user = getUserById(userId);
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
        log.info("User ID: {} marked as INACTIVE", userId);

        UserSignupEvent event = createUserSignupEvent(user);
        event.setEventType(EventType.USER_DEACTIVATED);
        userEventProducer.sendUserSignupEvent(event);
        log.info("User deactivation event sent to Kafka for user ID: {}", userId);

        return ResponseDTO.builder().message("User deactivated successfully").build();
    }

    @Override
    public UserDTO updateNotificationField(String userId, String fieldName, Boolean value) {
        log.info("Updating notification setting [{}] to [{}] for user ID: {}", fieldName, value, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        switch (fieldName) {
            case "commentMentions" -> user.setCommentMentions(value);
            case "taskUpdates" -> user.setTaskUpdates(value);
            case "bugUpdates" -> user.setBugUpdates(value);
            case "emailUpdates" -> user.setEmailUpdates(value);
            case "subtaskUpdates" -> user.setSubTaskUpdates(value);
            default -> throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }

        User modifiedUser = userRepository.save(user);
        log.info("Notification field [{}] updated successfully for user ID: {}", fieldName, userId);

        UserSignupEvent event = createUserSignupEvent(modifiedUser);
        event.setEventType(EventType.USER_UPDATED);
        userEventProducer.sendUserSignupEvent(event);
        log.info("User notification update event sent to Kafka for user ID: {}", userId);

        return convertToUserDTO(modifiedUser);
    }
}
