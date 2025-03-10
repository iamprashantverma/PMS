package com.pms.userservice.services.impl;

import com.pms.userservice.dto.ResponseDTO;
import com.pms.userservice.dto.UserDTO;
import com.pms.userservice.entities.User;
import com.pms.userservice.entities.enums.Roles;
import com.pms.userservice.entities.enums.Status;
import com.pms.userservice.exceptions.ResourceNotFound;
import com.pms.userservice.repositories.UserRepository;
import com.pms.userservice.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

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
    public ResponseDTO updateUserDetails(UserDTO userDTO) {
        User existingUser = getUserById(userDTO.getUserId());
        modelMapper.map(userDTO, existingUser);
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
    @Transactional
    public ResponseDTO assignEpicToUser(String epicId, String userId) {
        User user = getUserById(userId);
        user.getEpicIds().add(epicId);
        userRepository.save(user);
        return ResponseDTO.builder().message("Epic assigned to user successfully").build();
    }

    @Override
    @Transactional
    public ResponseDTO assignTaskToUser(String taskId, String userId) {
        User user = getUserById(userId);
        user.getTaskIds().add(taskId);
        userRepository.save(user);
        return ResponseDTO.builder().message("Task assigned to user successfully").build();
    }

    @Override
    @Transactional
    public ResponseDTO assignBugToUser(String bugId, String userId) {
        User user = getUserById(userId);
        user.getBugIds().add(bugId);
        userRepository.save(user);
        return ResponseDTO.builder().message("Bug assigned to user successfully").build();
    }

    @Override
    @Transactional
    public ResponseDTO assignSubTaskToUser(String subTaskId, String userId) {
        User user = getUserById(userId);
        user.getSubTaskIds().add(subTaskId);
        userRepository.save(user);
        return ResponseDTO.builder().message("Subtask assigned to user successfully").build();
    }

    @Override
    @Transactional
    public ResponseDTO removeUserFromBug(String bugId, String userId) {
        User user = getUserById(userId);
        user.getBugIds().remove(bugId);
        userRepository.save(user);
        return ResponseDTO.builder().message("User removed from bug successfully").build();
    }

    @Override
    @Transactional
    public ResponseDTO removeUserFromProject(String projectId, String userId) {
        User user = getUserById(userId);
        user.getProjectIds().remove(projectId);
        userRepository.save(user);
        return ResponseDTO.builder().message("User removed from project successfully").build();
    }

    @Override
    @Transactional
    public ResponseDTO removeUserFromSubTask(String subTaskId, String userId) {
        User user = getUserById(userId);
        user.getSubTaskIds().remove(subTaskId);
        userRepository.save(user);
        return ResponseDTO.builder().message("User removed from subtask successfully").build();
    }

    @Override
    @Transactional
    public ResponseDTO removeUserFromEpic(String epicId, String userId) {
        User user = getUserById(userId);
        user.getEpicIds().remove(epicId);
        userRepository.save(user);
        return ResponseDTO.builder().message("User removed from epic successfully").build();
    }

    @Override
    @Transactional
    public ResponseDTO removeUserFromTask(String taskId, String userId) {
        User user = getUserById(userId);
        user.getTaskIds().remove(taskId);
        userRepository.save(user);
        return ResponseDTO.builder().message("User removed from task successfully").build();
    }
}
