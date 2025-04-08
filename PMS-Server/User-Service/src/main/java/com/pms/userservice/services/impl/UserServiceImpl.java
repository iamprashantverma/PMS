package com.pms.userservice.services.impl;

import com.pms.userservice.dto.ResponseDTO;
import com.pms.userservice.dto.UserDTO;
import com.pms.userservice.entities.User;
import com.pms.userservice.entities.enums.Roles;
import com.pms.userservice.entities.enums.Status;
import com.pms.userservice.exceptions.ResourceNotFound;
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

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private User convertToUserEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    /* Get user by userId */
    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("Invalid User ID!"));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("Email not registered, please sign up!"));
    }

    @Override
    public List<Roles> getAllRoles() {
        return List.of(Roles.values());
    }

    @Override
    @Transactional
    public ResponseDTO assignRoleToUser(String userId, Roles role) {
        User user = getUserById(userId);
        user.setRole(role);
        userRepository.save(user);
        return ResponseDTO.builder().message("Role assigned successfully").build();
    }

    @Override
    public UserDTO getUserDetails(String userId) {
        return convertToUserDTO(getUserById(userId));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserDTO)
                .toList();
    }

    @Override
    @Transactional
    public ResponseDTO updateUserDetails(UserDTO userDTO, MultipartFile file) {

        User existingUser = getUserById(userDTO.getUserId());

        // change some only the particular information
        existingUser.setName(userDTO.getName());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setDob(userDTO.getDob());
        existingUser.setGender(userDTO.getGender());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhoneNo(userDTO.getPhoneNo());

        String profileUrl = cloudinaryService.uploadImage(file);
        existingUser.setImage(profileUrl);

        userRepository.save(existingUser);
        return ResponseDTO.builder().message("User details updated successfully").build();
    }

    @Override
    @Transactional
    public ResponseDTO deactivateUser(String userId) {
        User user = getUserById(userId);
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
        return ResponseDTO.builder().message("User deactivated successfully").build();
    }

    @Override
    public UserDTO updateNotificationField(String userId, String fieldName, Boolean value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        switch (fieldName) {
            case "commentMentions" -> user.setCommentMentions(value);
            case "taskUpdates" -> user.setTaskUpdates(value);
            case "bugUpdates" -> user.setBugUpdates(value);
            case "emailUpdates" -> user.setEmailUpdates(value);
            case "subtaskUpdates" ->user.setSubTaskUpdates(value);
            default -> throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }

        User modifiedUser = userRepository.save(user);
        return convertToUserDTO(modifiedUser);
    }
}

